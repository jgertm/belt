(ns belt.core
  (:gen-class)
  (:require [belt
             [actor :refer :all]
             [blockchain :refer :all]
             [transaction :refer :all]]
            [clojure.pprint :refer [pprint]]
            [clojure.core.async :as as]))

(comment (pprint genesis-blockchain )

         (issue-block prime-actor
                      genesis-blockchain
                      (request-registration some-actor))

         (as/<!! (:channel genesis-blockchain))

         )

(defn make-thread
  [index blockchain]
  (let [{:keys [chan] :as me} (make-actor)]
    (issue-block prime-actor blockchain (request-registration me))
    (as/go-loop [local-blockchain blockchain]
      (if-let [new-block (verify-block (as/<! chan))]
        (do (println index new-block)
            (recur (conj local-blockchain new-block)))
        (recur local-blockchain)))))

(defn prime-thread
  []
  (as/go-loop [local-blockchain genesis-blockchain]
    (let [new-block (as/<! (:chan prime-actor))]
      (println :prime new-block)
      (if (verify-block new-block)
        (do (println :prime :accept!)
            (recur (conj local-blockchain new-block)))
        (recur local-blockchain)))))

(prime-thread)

(as/>!! (:chan prime-actor) :hello)

(make-thread 0 genesis-blockchain)
