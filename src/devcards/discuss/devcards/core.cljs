(ns discuss.devcards.core
  (:require [devcards.core :as dc :refer-macros [defcard deftest defcard-om]]
            [cljs.test :refer-macros [testing is are]]
            [om.dom :as dom]
            [discuss.core :as core]
            [discuss.communication :as com]
            [discuss.debug :as debug]
            [discuss.extensions]
            [discuss.find :as find]
            [discuss.integration :as integration]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views :as views]))

(enable-console-print!)

(def test-state (atom {:val {:discussion {:add_premise_text "There are many parks in neighbouring towns holds, because...", :bubbles [{:votecounts 29, :votecounts_message "29 more participants with this opinion.", :data_argument_uid "None", :omit_url false, :data_is_supportive "True", :is_status false, :id "1464001655.6145825", :data_statement_uid "51", :is_user true, :is_info false, :data_type "statement", :url "http://localhost:4284/discuss/town-has-to-cut-spending", :is_system false, :message "<strong>There are many parks in neighbouring towns.</strong>"} {:votecounts 0, :votecounts_message "You are the first one with this opinion.", :data_argument_uid "None", :omit_url true, :data_is_supportive "None", :is_status true, :id "now", :data_statement_uid "None", :is_user false, :is_info false, :data_type "None", :url "None", :is_system false, :message "Now"} {:votecounts 0, :votecounts_message "You are the first one with this opinion.", :data_argument_uid "None", :omit_url true, :data_is_supportive "None", :is_status false, :id "1464001655.6119502", :data_statement_uid "None", :is_user false, :is_info false, :data_type "None", :url "None", :is_system true, :message "What is your most important reason why <strong> there are many parks in neighbouring towns</strong> holds? <br>Because..."} {:votecounts 0, :votecounts_message "You are the first one with this opinion.", :data_argument_uid "None", :omit_url false, :data_is_supportive "None", :is_status false, :id "end", :data_statement_uid "None", :is_user false, :is_info true, :data_type "None", :url "None", :is_system false, :message "You are the first one, who said that <strong>There are many parks in neighbouring towns</strong>.<br>Please enter your reason for your statement."}], :mode "justify", :is_supportive true, :save_statement_url "ajax_set_new_start_statement", :search {:issue "1", :distance_name "Levensthein", :values [{:distance "00038", :url "api/town-has-to-cut-spending/justify/53/t", :statement_uid 53, :index 0, :text "Parks are very important for our climate."} {:distance "00040", :url "api/town-has-to-cut-spending/justify/51/t", :statement_uid 51, :index 1, :text "There are many parks in neighbouring towns."} {:distance "00067", :url "api/town-has-to-cut-spending/justify/44/t", :statement_uid 44, :index 2, :text "Spending of the city for these festivals are higher than the earnings."} {:distance "00081", :url "api/town-has-to-cut-spending/justify/54/t", :statement_uid 54, :index 3, :text "Our swimming pools are very old and it would take a major investment to repair them."}]}}, :issues {:date "3 hours ago", :slug "town-has-to-cut-spending", :uid 1, :all [{:uid "1", :url "", :arg_count 18, :title "Town has to cut spending ", :date "3 hours ago", :slug "town-has-to-cut-spending", :enabled "disabled", :info "Our town needs to cut spending. Please discuss ideas how this should be done."} {:uid "2", :url "api/cat-or-dog", :arg_count 30, :title "Cat or Dog", :date "3 hours ago", :slug "cat-or-dog", :enabled "enabled", :info "Your familiy argues about whether to buy a cat or dog as pet. Now your opinion matters!"} {:uid "3", :url "api/elektroautos", :arg_count 9, :title "Elektroautos", :date "3 hours ago", :slug "elektroautos", :enabled "enabled", :info "Elektroautos - Die Autos der Zukunft? Bitte diskutieren Sie dazu."} {:uid "4", :url "api/unterstuumltzung-der-sekretariate", :arg_count 0, :title "Unterst&uuml;tzung der Sekretariate", :date "3 hours ago", :slug "unterstuumltzung-der-sekretariate", :enabled "enabled", :info "Unsere Sekretariate in der Informatik sind arbeitsm&auml;&szlig;ig stark &uuml;berlastet. Bitte diskutieren Sie Ma&szlignahmen zur Unterst&uuml;tzung der Sekretariate."}], :title "Town has to cut spending ", :info "Our town needs to cut spending. Please discuss ideas how this should be done.", :intro "Current discussion is about", :tooltip "The discussion was started 3 hours ago and already has 18 argument.", :arg_count 18}, :items [{:url "add", :already_used_text "", :id "item_start_premise", :already_used false, :premises [{:id 0, :title "None of the above! Let me state my own reason!"}], :attitude "justify"}], :layout {:loading? false, :add? false, :add-type :add-justify-premise, :template :discussion, :add-text "Let me enter my reason!", :title "discuss", :error-msg nil, :reference "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt", :intro "The current discussion is about:", :error? false}, :debug {:last-api "api/get/statements/1/3/are", :response {:issues {:date "3 hours ago", :slug "town-has-to-cut-spending", :uid 1, :all [{:uid "1", :url "", :arg_count 18, :title "Town has to cut spending ", :date "3 hours ago", :slug "town-has-to-cut-spending", :enabled "disabled", :info "Our town needs to cut spending. Please discuss ideas how this should be done."} {:uid "2", :url "api/cat-or-dog", :arg_count 30, :title "Cat or Dog", :date "3 hours ago", :slug "cat-or-dog", :enabled "enabled", :info "Your familiy argues about whether to buy a cat or dog as pet. Now your opinion matters!"} {:uid "3", :url "api/elektroautos", :arg_count 9, :title "Elektroautos", :date "3 hours ago", :slug "elektroautos", :enabled "enabled", :info "Elektroautos - Die Autos der Zukunft? Bitte diskutieren Sie dazu."} {:uid "4", :url "api/unterstuumltzung-der-sekretariate", :arg_count 0, :title "Unterst&uuml;tzung der Sekretariate", :date "3 hours ago", :slug "unterstuumltzung-der-sekretariate", :enabled "enabled", :info "Unsere Sekretariate in der Informatik sind arbeitsm&auml;&szlig;ig stark &uuml;berlastet. Bitte diskutieren Sie Ma&szlignahmen zur Unterst&uuml;tzung der Sekretariate."}], :title "Town has to cut spending ", :info "Our town needs to cut spending. Please discuss ideas how this should be done.", :intro "Current discussion is about", :tooltip "The discussion was started 3 hours ago and already has 18 argument.", :arg_count 18}, :items [{:url "add", :already_used_text "", :id "item_start_premise", :already_used false, :premises [{:id 0, :title "None of the above! Let me state my own reason!"}], :attitude "justify"}], :extras {:is_reportable false, :lang_is_de false, :link_en_class "active", :lang_is_en true, :nickname "Christian", :is_user_neutral false, :close_premise_container false, :is_user_female false, :logged_in true, :users_avatar "https://secure.gravatar.com/avatar/5fb6235d584543384ee61ac6814195bb?s=80&d=wavatar", :link_de_class "", :users_name "Christian", :show_bar_icon false, :is_user_male true, :restart_url "api/town-has-to-cut-spending", :add_premise_container_style "", :add_statement_container_style "display: none", :is_editable false, :show_display_style false}, :discussion {:add_premise_text "There are many parks in neighbouring towns holds, because...", :bubbles [{:votecounts 29, :votecounts_message "29 more participants with this opinion.", :data_argument_uid "None", :omit_url false, :data_is_supportive "True", :is_status false, :id "1464001655.6145825", :data_statement_uid "51", :is_user true, :is_info false, :data_type "statement", :url "http://localhost:4284/discuss/town-has-to-cut-spending", :is_system false, :message "<strong>There are many parks in neighbouring towns.</strong>"} {:votecounts 0, :votecounts_message "You are the first one with this opinion.", :data_argument_uid "None", :omit_url true, :data_is_supportive "None", :is_status true, :id "now", :data_statement_uid "None", :is_user false, :is_info false, :data_type "None", :url "None", :is_system false, :message "Now"} {:votecounts 0, :votecounts_message "You are the first one with this opinion.", :data_argument_uid "None", :omit_url true, :data_is_supportive "None", :is_status false, :id "1464001655.6119502", :data_statement_uid "None", :is_user false, :is_info false, :data_type "None", :url "None", :is_system true, :message "What is your most important reason why <strong> there are many parks in neighbouring towns</strong> holds? <br>Because..."} {:votecounts 0, :votecounts_message "You are the first one with this opinion.", :data_argument_uid "None", :omit_url false, :data_is_supportive "None", :is_status false, :id "end", :data_statement_uid "None", :is_user false, :is_info true, :data_type "None", :url "None", :is_system false, :message "You are the first one, who said that <strong>There are many parks in neighbouring towns</strong>.<br>Please enter your reason for your statement."}], :mode "justify", :is_supportive true, :save_statement_url "ajax_set_new_start_statement"}}}, :user {:nickname "Christian", :token "Christian-f96bad3afea854b9e2781a235f39d90ce32ed80e80732cf08defa279b202cbb0f3a811bc4d8150b2ad1463c21f45a36f1f5a444c52f19c09359e3bc8f9cc9dd5", :logged-in? true, :mouse-x 445, :mouse-y 783}, :clipboard {:selections nil, :current nil}, :sidebar {:show? true}}}))

(defcard main
         (dc/om-root views/main-view)
         lib/app-state
         #_{:history true})

(defcard control-buttons
         ""
         (dom/div nil
                  (dom/button #js {:className "btn btn-info"
                                   :onClick   core/main}
                              (dom/i #js {:className "fa fa-fort-awesome"}))
                  (debug/control-buttons lib/app-state)))

(defcard find-form
         ""
         (dc/om-root find/form-view))

(defcard-om find-statement
            "Query database to find statements."
            find/results-view
            lib/app-state)

(deftest find-tests
         "Testing the small search engine in `discuss.find`"
         (testing "fn statement, sending request and counting results"
           (is (= 4 (count (find/get-search-results)))))
         test-state)

(deftest integration-test
         "Testing `discuss.integration`"
         (let [doms-raw (.getElementsByTagName js/document "*")]
           (testing "fn get-parent"
             (is (= "test-get-parent" (.-id (integration/get-parent doms-raw "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"))))
             (is (= "figwheel-heads-up-content-area" (.-id (integration/get-parent doms-raw "")))))))

(deftest lib-test
         (testing "Convert strings to integer."
           (is (= 4
                 (lib/str->int "4")))
           (is (= 9999999999999999999999999
                  (lib/str->int "9999999999999999999999999")))
           (is (nil? (lib/str->int "")))
           (is (nil? (lib/str->int "foo"))))

         (testing "cljs to json conversion stuff."
           (let [json "{\"distance_name\": \"Levensthein\", \"values\": {\"00020_020\": \"Foobaraaaaaaaaaaaaaa.\", \"00033_013\": \"This is the only park in our city.\"}}"]
             (is (= {:distance_name "Levensthein", :values {:00020_020 "Foobaraaaaaaaaaaaaaa.", :00033_013 "This is the only park in our city."}} (lib/json->clj json)))))

         (testing "Get issue for uid."
           (is (= "Elektroautos"
                  (:title (lib/get-issue 3))))
           (is (= "3"
                  (:uid (lib/get-issue "Elektroautos"))))
           (is (nil? (lib/get-issue "This should not be an issue in our app-state"))))

         (testing "Get reference by id."
           (let [refs [{:url "api/town-has-to-cut-spending/justify/70/t", :uid 1, :text "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"} {:url "api/elektroautos/justify/72/t", :uid 3, :text "Es sei &quot;ein gutes Gefühl&quot;, sagt Vater Richard Dolphin\n"} {:url "api/town-has-to-cut-spending/justify/73/t", :uid 4, :text "meisten Nachbarkommunen"} {:url "api/town-has-to-cut-spending/justify/74/t", :uid 5, :text "deutschen Kommunen"} {:url "api/town-has-to-cut-spending/justify/75/t", :uid 6, :text "Es sei &quot;ein gutes Gefühl&quot;, sagt Vater Richard Dolphin "} {:url "api/town-has-to-cut-spending/justify/76/t", :uid 7, :text "Tatsächlich ist Düsseldorf ein außergewöhnlicher Fall unter den deutschen Kommunen . Knapp 300 Millionen Euro plant die Stadt in diesem Jahr für Kinderfeste, Jugendbücher und Betreuungseinrichtungen ein, selten hat sie so viel für Familien ausgegeben. Vor ein paar Monaten wurden gar die Kindergartengebühren für Drei- bis Sechsjährige abgeschafft. Sparen muss Düsseldorf nicht, der Haushalt für das Jahr 2010 ist ausgeglichen, ein Kunststück, das der Stadt bereits zum elften Mal in Folge gelingt. Ja, mehr noch: Seit 2007 ist die Stadt schuldenfrei, inzwischen hat sie sich sogar ein ansehnliches Polster angespart.\n\nEinige Städte haben im vergangenen Aufschwung immerhin jedes Jahr mehr Geld eingenommen als ausgegeben. Doch die meisten Nachbarkommunen  von Düsseldorf stehen kurz vor dem Zusammenbruch, wie sich auch aus einer gerade erschienenen Studie der Bertelsmann-Stiftung ersehen lässt. &quot;Die Schere zwischen armen und reichen Kommunen geht weiter auseinander&quot;, schreiben die Autoren. Es stelle sich die Frage, wie den rund 60 besonders armen Städten in Nordrhein-Westfalen der Haushaltsausgleich gelingen solle, wenn &quot;sie nicht einmal während einer beispiellosen Wachstumsphase&quot; der vergangenen Jahre dazu in der Lage waren. Die Hälfte der Städte im Bundesland hat"}]]
             (is (= {:url "api/town-has-to-cut-spending/justify/70/t", :uid 1, :text "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"}
                    (lib/get-reference 1 refs)))
             (is (= {:url "api/town-has-to-cut-spending/justify/70/t", :uid 1, :text "Eine Krise in den neunziger Jahren brachte die Wende für die Stadt"}
                    (lib/get-reference "1" refs)))
             (is (nil? (lib/get-reference "foo" refs)))
             (is (nil? (lib/get-reference -1 refs)))))

         test-state)