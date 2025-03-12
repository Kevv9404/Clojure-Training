(ns model.paddle
  (:require [model.terminal :as terminal]))

(defn get-top-y [{:paddle/keys [height position]}] (- (second position) (/ height 2)))

(defn get-bottom-y [{:paddle/keys [height position]}] (+ (second position) (/ height 2)))

(defn left-paddle? [{:paddle/keys [position]}] (< (first position) 5))

(defn y-position [{:paddle/keys [position]}] (second position))

(defn x-position [{:paddle/keys [position]}] (first position))

(defn ball-colliding-paddle? [paddle ball]
  (let [ball-position (:ball/position ball)
        [b-x b-y] ball-position]
    (cond (and (left-paddle? paddle) (<= b-x (x-position paddle)) (>= b-y (get-top-y paddle)) (<= b-y (get-bottom-y paddle)) (< (first (:ball/velocity ball)) 0))
          true
          (and (not (left-paddle? paddle)) (>= b-x (x-position paddle)) (>= b-y (get-top-y paddle)) (<= b-y (get-bottom-y paddle)) (> (first (:ball/velocity ball)) 0))
          true
          :else false)))

(defn move-up [{:paddle/keys [height] :as paddle}]
  (if (> (y-position paddle) (/ height 2))
    (update-in paddle [:paddle/position 1] dec)
    paddle))

(defn move-down [paddle terminal]
  (if (< (get-bottom-y paddle) (terminal/get-terminal-height terminal))
    (update-in paddle [:paddle/position 1] inc)
    paddle))

(defn draw-paddle! [paddle terminal]
  (loop [y (get-top-y paddle)]
    (when (<= y (get-bottom-y paddle))
      (terminal/put-character terminal (x-position paddle) y \|)
      (recur (inc y)))))



