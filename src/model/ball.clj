(ns model.ball
  (:require [model.utils :as utils]))

(defn setup-ball []
  {:ball/position [0 0]
   :ball/velocity [0 0]})

(defn update-position [{:ball/keys [position velocity]} n]
  (utils/addition-of-vectors position (utils/multiply-vector velocity n)))

(defn change-sign [n] (- n))

(defn bounce [{:ball/keys [velocity] :as ball} target-axis]
  (if (= target-axis :x)
    (update-in ball [:ball/velocity 0] change-sign)
    (update-in ball [:ball/velocity 1] change-sign)))
