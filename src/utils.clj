(ns utils)

(defn addition-of-vectors
  "Returns a new vector with the sum of  x's and y's of two vectors as arguments"
  [vec1 vec2]
  (mapv + vec1 vec2))


(defn multiply-vector
  "Returs a new vector of the multiplication of x, y times n"
  [vec n]
  (mapv #(* % n) vec))
