(ns belt.blockchain
  (:require [belt
             [actor :refer :all]
             [transaction :refer :all]]
            [clojure.core.async :as as]
            [schema.core :as s]
            [clj-pgp.core :as pgp]
            [clj-pgp.signature :as pgp-sig])
  (:import [belt.actor Actor #_Statement]
           [belt.transaction Block RegisterActor]))

(s/defschema Blockchain
  [(s/protocol Transaction)])

(def empty-blockchain
  [])

(def genesis-blockchain
  [(request-registration prime-actor)])

(defn actor->chan
  [blockchain]
  (->> blockchain
       (filter (partial instance? RegisterActor))
       (mapcat (juxt :uuid :chan))
       (apply hash-map)))

(s/defn issue-block :- Blockchain
  [me         :- Actor
   blockchain :- Blockchain
   new-transaction  :- Transaction]
  (doseq [chan (-> blockchain actor->chan vals)]
    (as/>!! chan (issue new-transaction blockchain me))))

(defn actor->pubkey
  [blockchain]
  (->> blockchain
       (filter (partial instance? RegisterActor))
       (mapcat (juxt :uuid :pubkey))
       (apply hash-map)))

(s/defn verify-block :- (s/protocol Transaction)
  [blockchain :- Blockchain
   {:keys [issuer content hmac]
    :as statement} :- Block]
  (when (and (contains? (actor->pubkey blockchain) issuer)
             (pgp-sig/verify (->> content (conj blockchain) pr-str)
                             hmac
                             (get actor->pubkey issuer)))
    content))
