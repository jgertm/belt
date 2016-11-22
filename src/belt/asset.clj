(ns belt.asset
  (:require [schema.core :as s]))

(s/defrecord Asset
    [name        :- String
     ;; owner       :- s/Uuid
     description :- String
     value       :- Integer])
