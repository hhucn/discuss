(ns discuss.utils.common-test
  (:require [cljs.test :refer-macros [deftest is are testing]]
            [clojure.spec.test.alpha :as stest]
            [discuss.utils.common :as lib]
            [discuss.test.lib :as tlib]))

(deftest conversions
  (testing "Convert strings to integer."
    (is (= 4
           (lib/str->int "4")))
    (is (= 9999999999999999999999999
           (lib/str->int "9999999999999999999999999")))
    (is (nil? (lib/str->int "")))
    (is (nil? (lib/str->int "foo"))))

  (testing "cljs to json conversion stuff."
    (let [json "{\"distance_name\": \"Levensthein\", \"values\": {\"00020_020\": \"Foobaraaaaaaaaaaaaaa.\", \"00033_013\": \"This is the only park in our city.\"}}"]
      (is (= {:distance_name "Levensthein", :values {:00020_020 "Foobaraaaaaaaaaaaaaa.", :00033_013 "This is the only park in our city."}} (lib/json->clj json))))))

(deftest reference-handling
  (testing "Get reference by id."
    (let [refs [{:url "api/town-has-to-cut-spending/justify/70/t", :uid 1, :text "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"} {:url "api/elektroautos/justify/72/t", :uid 3, :text "Es sei &quot;ein gutes Gefühl&quot;, sagt Vater Richard Dolphin\n"} {:url "api/town-has-to-cut-spending/justify/73/t", :uid 4, :text "meisten Nachbarkommunen"} {:url "api/town-has-to-cut-spending/justify/74/t", :uid 5, :text "deutschen Kommunen"} {:url "api/town-has-to-cut-spending/justify/75/t", :uid 6, :text "Es sei &quot;ein gutes Gefühl&quot;, sagt Vater Richard Dolphin "} {:url "api/town-has-to-cut-spending/justify/76/t", :uid 7, :text "Tatsächlich ist Düsseldorf ein außergewöhnlicher Fall unter den deutschen Kommunen . Knapp 300 Millionen Euro plant die Stadt in diesem Jahr für Kinderfeste, Jugendbücher und Betreuungseinrichtungen ein, selten hat sie so viel für Familien ausgegeben. Vor ein paar Monaten wurden gar die Kindergartengebühren für Drei- bis Sechsjährige abgeschafft. Sparen muss Düsseldorf nicht, der Haushalt für das Jahr 2010 ist ausgeglichen, ein Kunststück, das der Stadt bereits zum elften Mal in Folge gelingt. Ja, mehr noch: Seit 2007 ist die Stadt schuldenfrei, inzwischen hat sie sich sogar ein ansehnliches Polster angespart.\n\nEinige Städte haben im vergangenen Aufschwung immerhin jedes Jahr mehr Geld eingenommen als ausgegeben. Doch die meisten Nachbarkommunen  von Düsseldorf stehen kurz vor dem Zusammenbruch, wie sich auch aus einer gerade erschienenen Studie der Bertelsmann-Stiftung ersehen lässt. &quot;Die Schere zwischen armen und reichen Kommunen geht weiter auseinander&quot;, schreiben die Autoren. Es stelle sich die Frage, wie den rund 60 besonders armen Städten in Nordrhein-Westfalen der Haushaltsausgleich gelingen solle, wenn &quot;sie nicht einmal während einer beispiellosen Wachstumsphase&quot; der vergangenen Jahre dazu in der Lage waren. Die Hälfte der Städte im Bundesland hat"}]]
      (is (= {:url "api/town-has-to-cut-spending/justify/70/t", :uid 1, :text "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"}
             (lib/get-reference 1 refs)))
      (is (= {:url "api/town-has-to-cut-spending/justify/70/t", :uid 1, :text "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"}
             (lib/get-reference "1" refs)))
      (is (nil? (lib/get-reference "foo" refs)))
      (is (nil? (lib/get-reference -1 refs))))))

(deftest react-key
  (testing "Generate unique react-key"
    (is (not= (lib/get-unique-key) (lib/get-unique-key))))

  (testing "Add :key to given dictionary with a unique key."
    (is (not (nil? (:key (lib/merge-react-key {:foo "bar"})))))))

(deftest string-conversions
  (testing "Pluralize words if input is greater one."
    (is (= "entries" (lib/singular->plural 2 "entry")))
    (is (= "entry" (lib/singular->plural 1 "entry")))
    (is (= "entries" (lib/singular->plural 0 "entry")))
    (is (nil? (lib/singular->plural -1 "entry")))
    (is (= "may-the-force-be-with-you" (lib/singular->plural 1 "may-the-force-be-with-you")))
    (is (= "may-the-force-be-with-yous" (lib/singular->plural 101000 "may-the-force-be-with-you"))) ;; Strange :D
    (is (nil? (lib/singular->plural -1 -1)))))

(deftest json-conversions
  (testing "Conversions of JSON to Clojure data structures."
    (is (= [{:author {:nickname "kangaroo", :uid -1}}]
           (lib/json->clj [{"author" {"nickname" "kangaroo", "uid" -1}}])))
    (is (= {:author {:nickname "kangaroo", :uid -1}}
           (lib/json->clj {"author" {"nickname" "kangaroo", "uid" -1}})))
    (is (= {}
           (lib/json->clj {})))
    (is (= {:groot? true}
           (lib/json->clj {"groot?" true})
           (lib/json->clj "{\"groot?\": true}")))))

(deftest trim-strings
  (testing "Remove trailing whitespace and inline newlines."
    (tlib/check' (stest/check `discuss.utils.common/trim-and-normalize))
    (is (= "foo bar"
           (lib/trim-and-normalize "foo\nbar")
           (lib/trim-and-normalize "foo\r\fbar")
           (lib/trim-and-normalize "foo\t\t\t\tbar")
           (lib/trim-and-normalize "    foo bar   ")
           (lib/trim-and-normalize "\n\nfoo\t\t\t\tbar\n ")))))

(deftest test-origins
  (testing "Add an origin and remove it back again."
    (let [origin {:author "kangaroo", :content "penguins and stuff", :aggregate-id "huepfer.verlag", :entity-id 42, :version 100}]
      (lib/store-origin! origin)
      (is (= origin (lib/get-origin)))
      (lib/remove-origin!)
      (is (nil? (lib/get-origin))))))
