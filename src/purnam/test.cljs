(ns purnam.test
  (:require [purnam.test.common :refer [js-equals]]))

(if-not (.-jasmine js/window)
    (throw (ex-info "No Jasmine Framework Library Installed. Tests Cannot Proceed" {})))

(defn trim-quote [s]
 (second (re-find #"^\'(.*)\'$" s)))

(defn to-satisfy-compare [actual expected]
  (cond (= (js/goog.typeOf expected) "array")
        (js-equals expected actual)

        (fn? expected)
        (expected actual)

        :else
        (or (= expected actual)
            (js-equals expected actual))))

(defn to-satisfy-message [result actual expected eq-testers]
  (let [actualText (str actual)
        actualText (if (= actualText "[object Object]")
                     (let [ks (js/goog.object.getKeys actual)
                           vs (js/goog.object.getValues actual)]
                       (into {} (map (fn [x y] [x y])
                                     ks vs)))
                     actualText)
        notText (str (if (.-pass result) "Not " "") expected)]
    (aset result "message"
          (str "Expression: " eq-testers ;;(trim-quote tactual)
               "\n  Expected: " notText ;;(trim-quote texpected)
               "\n  Result: " actualText))))

(defn to-satisfy [util eq-testers]
  #js {:compare (fn [actual expected]
                  (let [result (js-obj)]
                    (aset result "pass" (to-satisfy-compare actual expected))
                    (to-satisfy-message result actual expected eq-testers)
                    result))})

(js/beforeEach
 (fn []
   (.addMatchers js/jasmine #js {:toSatisfy to-satisfy})))
