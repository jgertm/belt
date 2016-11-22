(ns belt.transaction
  (:require [belt.actor :refer :all]
            [clj-pgp.signature :as pgp-sig]
            [schema.core :as s]
            [clj-pgp.core :as pgp])
  (:import belt.actor.Actor))

(defprotocol Transaction
  (verify [this blockchain])
  (issue [this blockchain me]))

(s/defrecord Block
    [issuer  :- s/Uuid
     content :- (s/protocol Transaction)
     hmac    :- String])

(s/defrecord RegisterActor
    [uuid   :- s/Uuid
     chan
     pubkey :- String]

  Transaction
  (verify [this blockchain]
    (->> blockchain
         (filter #(instance? RegisterActor))
         (not-any? (partial = this))))
  (issue [this {:keys [blocks]} me]
    (map->Block
     {:issuer  (:uuid me)
      :content this
      :hmac    (pgp-sig/sign (->> this (conj blocks) pr-str)
                             (:prvkey me))})))

(s/defn request-registration :- RegisterActor
  [actor :- Actor]
  (map->RegisterActor
   (dissoc actor :prvkey)))
