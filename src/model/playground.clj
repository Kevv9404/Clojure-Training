(ns model.playground
  (:require [model.ball :refer :all]
            [model.terminal :as terminal]
            [model.paddle :as paddle]))

(defn setup-players [height]
  {:players [{:player-one {:paddle/position [5 12]
                           :paddle/height   height}}
             {:player-two {:paddle/position [75 12]
                           :paddle/height   height}}]})

(defn setup-game [paddle-height]
  (-> (setup-ball)
    (assoc :ball/position [40 12])
    (assoc :ball/velocity [1 1])
    (merge (setup-players paddle-height))))

(defn paddles-movement [k]
  (case k
    \k :up-player-one
    \j :down-player-one
    \w :up-player-two
    \s :down-player-two
    \q :quit
    "Unknown key"))

(defn get-command [terminal state]
  (when-let [k    (terminal/get-next-key-press terminal)]
    (fn [state]
      (cond
        (= (paddles-movement k) :up-player-one) (update-in state [:players 0 :player-one] paddle/move-up)
        (= (paddles-movement k) :down-player-one) (update-in state [:players 0 :player-one] #(paddle/move-down % terminal))

        (= (paddles-movement k) :up-player-two) (update-in state [:players 1 :player-two] paddle/move-up)
        (= (paddles-movement k) :down-player-two) (update-in state [:players 1 :player-two] #(paddle/move-down % terminal))))))

(defn draw! [state terminal]
  (let [ball       (:ball/position state)
        player-one (get-in state [:players 0 :player-one])
        player-two (get-in state [:players 1 :player-two])]

    ;; Clear the screen
    (.clearScreen terminal)

    ;; Draw the ball
    (let [[ball-x ball-y] ball]
      (terminal/put-character terminal ball-x ball-y \O))

    ;; Draw the paddles
    (paddle/draw-paddle! player-one terminal)
    (paddle/draw-paddle! player-two terminal)

    ;; Flush the terminal to update the display
    (terminal/flush! terminal)))


(defn play! [paddle-height]
  (let [fps           30
        time-delta    (/ 1 fps)
        time-delta-ms (* time-delta 100)
        app-state     (atom (setup-game paddle-height))
        t             (terminal/terminal)]
    (terminal/init-terminal t)
    (println (terminal/get-terminal-height t))
    (try
      (loop []
        (draw! @app-state t)

        ;; Process user input
        (when-let [command (get-command t @app-state)]
          (swap! app-state command))

        ;; Update ball position
        (swap! app-state (fn [state]
                           (assoc state :ball/position
                                        (update-position state time-delta))))

        ;; Check for collisions with paddles and walls
        (let [player-one      (get-in @app-state [:players 0 :player-one])
              player-two      (get-in @app-state [:players 1 :player-two])
              ball-state      (select-keys @app-state [:ball/position :ball/velocity])
              [_ ball-y] (:ball/position @app-state)
              terminal-height (terminal/get-terminal-height t)]

          ;; Paddle collisions
          (when (paddle/ball-colliding-paddle? player-one ball-state)
            (swap! app-state (fn [state] (bounce state :x))))
          (when (paddle/ball-colliding-paddle? player-two ball-state)
            (swap! app-state (fn [state] (bounce state :x))))

          ;; Wall collisions (top and bottom)
          (when (or (<= ball-y 0) (>= ball-y (dec terminal-height)))
            (swap! app-state (fn [state] (bounce state :y)))))

        ;; Sleep to maintain frame rate
        (Thread/sleep time-delta-ms)

        ;; Continue the loop
        (recur))

      (finally
        ;; Clean up terminal when done
        (.exitPrivateMode t)))))

(comment
  (play! 5))

