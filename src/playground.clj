(ns playground
  (:require [model.ball :as ball]
            [terminal :as terminal]
            [model.paddle :as paddle]))

(defn setup-players [height]
  {:player-one (paddle/setup-paddle height [5 12])
   :player-two (paddle/setup-paddle height [75 12])})

(defn setup-game [paddle-height]
  (-> (ball/setup-ball)
    (assoc :ball/position [40 12])
    (assoc :ball/velocity [1 1])
    (merge (setup-players paddle-height))))


(defn get-command [t state]
  (when-let [key (terminal/get-next-key-press t)]
    (fn [state]
      (cond
        (= key \k) (update state :player-one paddle/move-up)
        (= key \j) (update state :player-one #(paddle/move-down % t))
        (= key \s) (update state :player-two paddle/move-up)
        (= key \w) (update state :player-two #(paddle/move-down % t))
        (= key \q) (terminal/close-terminal t)))))

(defn draw! [state terminal]
  (let [ball       (:ball/position state)
        player-one (get state :player-one)
        player-two (get state :player-two)]

    ;; Clear the screen
    (.clearScreen terminal)

    ;; Draw the ball
    (ball/draw-ball terminal ball)

    ;; Draw the paddles
    (paddle/draw-paddle player-one terminal)
    (paddle/draw-paddle player-two terminal)

    ;; Flush the terminal to update the display
    (terminal/flush! terminal)))


(defn process-user-input! [app-state t]
  (when-let [command (get-command t @app-state)]
    (swap! app-state command)))

(defn update-ball-position! [app-state time-delta]
  (swap! app-state (fn [state]
                     (assoc state :ball/position
                                  (ball/update-position state time-delta)))))

(defn handle-collisions! [app-state t]
  (let [player-one      (get @app-state :player-one)
        player-two      (get @app-state :player-two)
        ball-state      (select-keys @app-state [:ball/position :ball/velocity])
        [_ ball-y] (:ball/position @app-state)
        terminal-height (terminal/get-terminal-height t)]

    ;; Paddle collisions
    (when (paddle/ball-colliding-paddle? player-one ball-state)
      (swap! app-state (fn [state] (ball/bounce state :x))))
    (when (paddle/ball-colliding-paddle? player-two ball-state)
      (swap! app-state (fn [state] (ball/bounce state :x))))

    ;; Wall collisions (top and bottom)
    (when (or (<= ball-y 0) (>= ball-y (dec terminal-height)))
      (swap! app-state (fn [state] (ball/bounce state :y))))))

(defn game-loop [app-state t time-delta-ms time-delta]
  (loop []
    (draw! @app-state t)
    (process-user-input! app-state t)
    (update-ball-position! app-state time-delta)
    (handle-collisions! app-state t)
    (Thread/sleep time-delta-ms)
    (recur)))

(defn play! [paddle-height]
  (let [fps           30
        time-delta    (/ 1 fps)
        time-delta-ms (* time-delta 100)
        app-state     (atom (setup-game paddle-height))
        t             (terminal/terminal)]
    (terminal/init-terminal t)
    (try
      (game-loop app-state t time-delta-ms time-delta)
      (finally
        (.exitPrivateMode t)))))


;(defn play! [paddle-height]
;  (let [fps           30
;        time-delta    (/ 1 fps)
;        time-delta-ms (* time-delta 200)
;        app-state     (atom (setup-game paddle-height))
;        t             (terminal/terminal)]
;    (terminal/init-terminal t)
;    (try
;      (loop []
;        (draw! @app-state t)
;
;        ;; Process user input
;        (when-let [command (get-command t @app-state)]
;          (swap! app-state command))
;
;        ;; Update ball position
;        (swap! app-state (fn [state]
;                           (assoc state :ball/position
;                                        (update-position state time-delta))))
;
;        ;; Check for collisions with paddles and walls
;        (let [player-one      (get-in @app-state [:players 0 :player-one])
;              player-two      (get-in @app-state [:players 1 :player-two])
;              ball-state      (select-keys @app-state [:ball/position :ball/velocity])
;              [_ ball-y] (:ball/position @app-state)
;              terminal-height (terminal/get-terminal-height t)]
;
;          ;; Paddle collisions
;          (when (paddle/ball-colliding-paddle? player-one ball-state)
;            (swap! app-state (fn [state] (bounce state :x))))
;          (when (paddle/ball-colliding-paddle? player-two ball-state)
;            (swap! app-state (fn [state] (bounce state :x))))
;
;          ;; Wall collisions (top and bottom)
;          (when (or (<= ball-y 0) (>= ball-y (dec terminal-height)))
;            (swap! app-state (fn [state] (bounce state :y)))))
;
;        ;; Sleep to maintain frame rate
;        (Thread/sleep time-delta-ms)
;
;        ;; Continue the loop
;        (recur))
;
;      (finally
;        ;; Clean up terminal when done
;        (.exitPrivateMode t)))))



(comment
  (play! 5))

