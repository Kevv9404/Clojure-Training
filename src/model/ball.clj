(ns model.ball)
(defn setup-ball []
  {:ball/position [0 0]
   :ball/velocity [0 0]})
(defn create-ball [x y]
  {:x  x
   :y  y
   :dx 1
   :dy 1
   })