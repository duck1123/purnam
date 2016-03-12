(ns purnam.test
  (:require [purnam.test.common :refer [js-equals]]))

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

(defn to-satisfy-message [result actual expected]
  (let [actualText (str actual)
        actualText (if (= actualText "[object Object]")
                     (let [ks (js/goog.object.getKeys actual)
                           vs (js/goog.object.getValues actual)]
                       (into {} (map (fn [x y] [x y])
                                     ks vs)))
                     actualText)
        notText (if (.-pass result) "Not " "")]
    (aset result "message"
          (str "Expression: " ;;(trim-quote tactual)
               "\n  Expected: " notText ;;(trim-quote texpected)
               "\n  Result: " actualText))))

(defn to-satisfy [util eq-testers]
  #js {:compare (fn [actual expected]
                  (let [result (js-obj)]
                    (aset result "pass" (to-satisfy-compare actual expected))
                    (to-satisfy-message result actual expected)
                    result))})

(js/beforeEach
 (fn []
   (.addMatchers js/jasmine #js {:toSatisfy to-satisfy})))
