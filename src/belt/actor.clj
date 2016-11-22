(ns belt.actor
  (:require [clj-pgp
             [core :as pgp]
             [generate :as pgp-gen]]
            [clj-uuid :as uuid]
            [clojure.core.async :as as]
            [schema.core :as s]))

(s/defrecord Actor
    [uuid    :- s/Uuid
     chan
     pubkey  :- org.bouncycastle.openpgp.PGPPublicKey
     prvkey  :- org.bouncycastle.openpgp.PGPPrivateKey])

(s/defn make-actor :- Actor []
  (let [uuid (uuid/v4)
        chan (as/chan)
        [pubkey prvkey]
        ((juxt pgp/public-key pgp/private-key) (pgp-gen/generate-keypair (pgp-gen/rsa-keypair-generator 2048) :rsa-general))]
    (->Actor uuid chan pubkey prvkey)))

(def prime-actor
  (make-actor))

(def some-actor
  (make-actor))
