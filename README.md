# Affari Tuoi CLI 📦

Una versione moderna e resiliente del celebre gioco "Affari Tuoi", sviluppata con il framework **Quarkus**.

## 🚀 Caratteristiche principali

* **Architettura Reattiva**: Gestione asincrona delle chiamate esterne tramite la libreria **Mutiny**.
* **Validazione della Messa in Onda**: Il gioco verifica automaticamente se oggi è un giorno di trasmissione (Lunedì-Venerdì) e consulta un'API esterna per escludere le festività nazionali italiane.
* **Motore Deterministico**: Implementazione di un sistema basato su *Seed* per il controllo della casualità.
* **Disaccoppiamento UI/Logica**: Netta separazione tra la logica di business (`Service`) e la presentazione a terminale (`UI`).

---

## 🎲 Il concetto di Seed

Il gioco supporta l'uso di un **Seed** per il generatore di numeri casuali per garantire la riproducibilità delle partite.



### Modalità di avvio
Il programma riconosce automaticamente se desideri una partita casuale o una specifica in base agli argomenti forniti:

1.  **Senza Seed (Partita Casuale)**:
    Eseguendo il comando senza numeri aggiuntivi, il sistema genererà un seed unico e imprevedibile utilizzando `SecureRandom`.
    ```bash
    java -jar target/quarkus-app/quarkus-run.jar
    ```

2.  **Con Seed (Partita Specifica)**:
    Passando un numero come argomento, la disposizione dei premi sarà deterministica. Utile per il debug.
    ```bash
    java -jar target/quarkus-app/quarkus-run.jar 987654321
    ```

---

## ⚙️ Installazione e Compilazione

### Requisiti
* **Java 21** o superiore.
* **Maven** 3.9+.

### 1. Compilazione del progetto
Prima di avviare il gioco, è necessario compilare il codice e generare il pacchetto eseguibile. Dalla cartella principale del progetto, esegui:

```bash
./mvnw clean package