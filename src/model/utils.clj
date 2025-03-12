(ns model.utils)
(defn addition-of-vectors
  "Returns a new vector with the sum of  x's and y's of two vectors as arguments"
  [vec1 vec2]
  (let [[x1 y1] vec1
        [x2 y2] vec2]
    [(+ x1 x2) (+ y1 y2)]))
(defn multiply-vector
  "Returs a new vector of the multiplication of x, y times n"
  [vec n]
  (let [[x y] vec]
    [(* x n) (* y n)]))