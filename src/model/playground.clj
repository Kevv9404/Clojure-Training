(ns model.playground
  (:require [model.ball :refer :all]))

(defn setup-players [height]
  {:players [{:player-one {:paddle/position [1 5]
                           :paddle/height   height}}
             {:player-two {:paddle/position [9 5]
                           :paddle/height   height}}]})

(defn setup-game [paddle-height]
  (merge (setup-ball)
    (setup-players paddle-height)))