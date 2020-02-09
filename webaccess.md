
# Links / Sourcen

## Offizielle Dokumente
* [Gesetzestext: BITV 2.0](https://www.gesetze-im-internet.de/bitv_2_0/BJNR184300011.html)
* [W3C: Web Contect Accessbility Guidelines (WCAG) 2.0](https://www.w3.org/TR/WCAG20/)
* [W3C: Web Contect Accessbility Guidelines (WCAG) 2.1](https://www.w3.org/TR/WCAG21/)
* [bitv-lotse.de (BM f Arbeit und Soziales): Das Konzept der WCAG 2.0 und der BITV 2.0](http://www.bitv-lotse.de/BL/DE/1_Einfuehrung/1_3_Konzept/1_3_konzept_node.html)

## Checklisten, Unterschiede etc.
* [accessibility-checklist.ch: Checkliste für WCAG 2.0](https://www.accessibility-checklist.ch/)
* [putzhuber.at: Checkliste für WCAG 2.1](https://www.putzhuber.at/wcag-2-1-a-checkliste/)
* [hellbusch.de: Unterschiede BITV 2.0 und WCAG 2.0](https://www.hellbusch.de/auf-zur-barrierefreien-version/)

## Tips für Entwicklung
* [W3C: Design and Develop Overview for Web Accessibility](https://www.w3.org/WAI/design-develop/)
* [W3C: Web Accessibility Tutorials](https://www.w3.org/WAI/tutorials/)
* [(Übersetzung von W3C): Wie man WCAG 2.0 erfüllt](https://www.einfach-fuer-alle.de/wcag2.0/uebersetzungen/How-to-Meet-WCAG-2.0/)

# Entwicklung

**Bilder etc.**

`title`: Titel des Bilds

`alt`: (Ausführlicher) Alternativtext als Beschreibung für das Bild

Wenn es sich um Layoutbilder handelt, die vom Screen Reader ignoriert werden sollen, soll/muss das HTML5-Attribut verwendet werden
`aria-hidden` [Quelle: nomensa.com](https://www.nomensa.com/blog/2017/how-improve-web-accessibility-hiding-elements)

Beispiel: `<p aria-hidden="true">This element is ignored by assistive technology</p>`

> Elements that are assigned with this attribute (and all the child elements) will be ignored by a screen reader as it will be invisible to them, but will be visible to sighted users. 



