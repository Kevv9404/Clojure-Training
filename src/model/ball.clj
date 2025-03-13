(ns model.ball
  (:require [terminal :as terminal]
            [utils :as utils]))

(defn setup-ball []
  {:ball/position [0 0]
   :ball/velocity [30 6]})

(defn update-position [{:ball/keys [position velocity]} n]
  (utils/addition-of-vectors position (utils/multiply-vector velocity n)))

(defn draw-ball [t [x y]]
  (terminal/put-character t x y \O))

(defn change-sign [n] (- n))

(defn bounce [state target-axis]
  (if (= target-axis :x)
    (update-in state [:ball/velocity 0] change-sign)
    (update-in state [:ball/velocity 1] change-sign)))

;(defn bounce [state axis]
;  (let [[vx vy] (:ball/velocity state)]
;    (assoc state :ball/velocity
;                 (case axis
;                   :x [(- vx) vy]
;                   :y [vx (- vy)]))))
