(ns model.utils-test
  (:require [model.utils :as utils]
            [fulcro-spec.core :refer [=> assertions specification]]))

(specification "addition-of-vectors" :focus
  (assertions
    "Returns a new vector by the sum of two vectors"
    (utils/addition-of-vectors [1 2] [3 4]) => [4 6]
    "Returns a new vector by the multiplication of a given number"
    (utils/multiply-vector [2 3] 6) => [12 18]

    )
    )
