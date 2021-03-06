# Vad är detta
Ett arbetsprov för G-Loot skrivet i Kotlin för Android platformen

# Teknikval

## RxJava2
	För att enkelt och snyggt kunna hantera trådar och asynkrona operationer på ett
	funktionellt sätt, används rxjava2

## ReKotlin
	Statehantering är ett enkelt sätt att se till att samma typ av värden sätts på ett ställe.
	Med hjälp av redux och UDF finns ReKotlin för att enkelt låta oss sätta upp states
	på ett sådant sätt att se till att nätverks-anrop och deras svar sätts på ett ställe
	och att detta ställe alltid kommer vara sanningen

	Som bonus finns även automatisk loggningsföljd med i och med hur man använder actions.
	Ett litet middleware kommer ta hand och visa i loggningsfönstret hur användaren rör sig i appen
## Retrofit2
	Retrofit2 är erkänt och beprövat i många skarpa projekt, dessutom så tillåter det
	att man kopplar ihop dess anrop med RxJava2 och på så sätt kan se till att hela stacken
	använder RxJava2 hela vägen ut.
## Moshi
	För att kunna serialisera och deserialisera JSON enkelt används Moshi som är en av de snabbare
	mapparna som finns till JSON och java
## Lottie
	För det lilla extra, så används lottie för att hantera animering och få snygga
	animationer från deras http://lottiefiles.com

# Uppbyggnad
* App
> 	Är endast till för att vara en tunn app, där man kan skriva över
> temafärger, eller speciella texter. Detta är 	alltså ett brandinglager
> för att underlätta byggen och leveranser, medför även att byggtiden
> förminskas 	när man bara vill testa tema/lokaliserings uppdatering

* Core

> 	Ansvarar för all huvudlogik och är basen för all applikationslogik

* Network
> 	Ansvarar för att separera ut nätverksanropen som är gjorda med
> Retrofit2 	Underlättar också separation av testning då detta kan
> skrivas direkt för modulen.

# Skapat av
Joakim Forslund 2018 - me@syrou.eu