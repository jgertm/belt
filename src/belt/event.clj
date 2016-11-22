(ns belt.event
  (:require [schema.core :as s]))

(s/defrecord Event
    [new-facts :- {s/Keyword s/Any}])
