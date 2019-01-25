(ns discuss.utils.faker
  (:require [cljs.spec.alpha :as s]))

(def sentences-from-index
  ["The Botanical Garden of the Heinrich-Heine-University was founded in 1973 and belongs to the Institute of Botany at HHU."
   "There are around 6000 different plant species domiciled."
   "Currently, the city council discusses to close the University Park, because of its high running expenses of about $100.000 per year."
   "But apparently there is an anonymous investor ensuring to pay the running costs for at least the next five years."
   "Thanks to this anonymous person, the city does not loose a beautiful park, but this again fires up the discussion about possible savings for the future."
   "Here is some more blind text, which could be interactively inserted into the current discussion to show its functionality."
   "Since we reset the database daily, it does not matter which arguments are inserted and used to test and play around with discuss."
   "Lorem ipsum dolor sit amet, consectetuer adipiscing elit."
   "Aenean commodo ligula eget dolor."
   "Aenean massa."
   "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus."
   "Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem."
   "Nulla consequat massa quis enim."
   "Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu."
   "In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo."
   "Nullam dictum felis eu pede mollis pretium."
   "Integer tincidunt."
   "Cras dapibus."
   "Vivamus elementum semper nisi."
   "Aenean vulputate eleifend tellus."
   "Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim."
   "Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus."
   "Phasellus viverra nulla ut metus varius laoreet."
   "Quisque rutrum."
   "Aenean imperdiet."
   "Etiam ultricies nisi vel augue."
   "Curabitur ullamcorper ultricies nisi."
   "Nam eget dui."
   "Etiam rhoncus."
   "Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum."
   "Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem."
   "Maecenas nec odio et ante tincidunt tempus."
   "Donec vitae sapien ut libero venenatis faucibus."
   "Nullam quis ante."
   "Etiam sit amet orci eget eros faucibus tincidunt."
   "Duis leo."])

(defn random-sentence
  "Return random sentence from website to fake selections."
  []
  (rand-nth sentences-from-index))
(s/fdef random-sentence
  :ret string?)
