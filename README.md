# DonatEAT

*Don't waste it. DonatEAT.*

CSIT 227 (Object-Oriented Programming) Capstone_GroupPings

DonatEAT is a Java Swing desktop app that connects food **suppliers** (restaurants, bakeries, supermarkets) with **recipient** institutions (orphanages, nursing homes, homeless shelters). Suppliers list surplus food, the app checks that it is safe to distribute, and recipients claim it. Claimed batches are then handed to a volunteer for delivery.

---

## 1. Our App Features

| Feature | Status |
| --- |--------|
| Role selection (Supplier / Recipient) and establishment selection | Done   |
| Supplier dashboard with three tabs: Donor Portal, Pantry Dispatch Feed, Active Deliveries | Done   |
| Listing perishable and non-perishable surplus food | Done   |
| Food safety rules per food type | Done   |
| Dispatch feed sorted by urgency or by volume (switchable at runtime) | Done   |
| Claiming a batch, assigning a volunteer, marking it delivered | Done   |
| Input validation with `JOptionPane` error dialogs | Done   |
| Saving and loading inventory to a text file | Done   |
| Recipient screen with urgency and volume tables and a Claim button | Done   |
| Login / Create account | Done   |
| Recipient screen connected to the real inventory | Done   |

## 2. Tech stack

- **Language:** Java 17 or newer
- **GUI:** Swing, laid out with the IntelliJ IDEA GUI Designer (`App.form`, `GridLayoutManager`)
- **IDE:** IntelliJ IDEA
- **Storage:** plain-text CSV file (`inventory_data.txt`), no database
- **External libraries:** none

## 3. App Flow

The whole app lives in **one window** (`App`, a `JFrame`). Each screen is a `JPanel` inside the root panel `pnlMain`, and only one is visible at a time.

```
selectWindow ("You are a:")
   |
   +-- Supplier --> supplierWindow (pick establishment) --> dashboardWindow
   |                    ^ Back                                  |
   |                                                            +-- Donor Portal
   |                                                            +-- Pantry Dispatch Feed
   |                                                            +-- Active Deliveries
   |
   +-- Recipient --> recipientWindow (pick establishment) --> mainRecipientWindow
                         ^ Back                                (urgency / volume tables)
```

| Window (panel) | What it does |
| --- | --- |
| `selectWindow` | Choose Supplier or Recipient. |
| `supplierWindow` | Choose a supplier establishment (Restaurant, Bakery, Supermarket). Confirm opens the dashboard. |
| `recipientWindow` | Choose a recipient establishment (Orphanage, Nursing Home, Homeless Shelter). |
| `dashboardWindow` | Supplier dashboard (see below). Has a **Back to Menu** button. |
| `mainRecipientWindow` | Recipient home with a strategy dropdown, two tables with checkboxes, and a Claim button. |

### Supplier dashboard tabs

| Tab | Purpose |
| --- | --- |
| **Donor Portal** | Form to add surplus: food type, name, quantity, hours until expiration (perishable) or days until best-before (non-perishable), and a flag (*Requires refrigeration* or *Packaging intact*). The labels change with the selected food type. |
| **Pantry Dispatch Feed** | Shows safe, still-available batches. The dropdown switches between *Urgency first* and *Volume first*. **Claim Selected** moves a batch to Active Deliveries. |
| **Active Deliveries** | Lists claimed and on-route batches. **Assign Volunteer** (needs a name) moves a batch to ON ROUTE. **Mark Delivered** finishes it. |

### Batch lifecycle

```
AVAILABLE --claim--> CLAIMED --assign volunteer--> ON ROUTE --mark delivered--> DELIVERED
```

## 4. Architecture

The code is split into four layers. The GUI never touches files or food objects directly; it goes through `DispatchManager`.

```
+--------------------------------------------------------------+
|  GUI layer            App, App.form, PanelManager            |
+------------------------------+-------------------------------+
                               |
+------------------------------v-------------------------------+
|  Logic layer          DispatchManager                        |
|                       DispatchStrategy (UrgencyFirst, Volume)|
+------------------------------+-------------------------------+
                               |
+------------------------------v-------------------------------+
|  Model layer          FoodItem                               |
|                       PerishableFood, NonPerishableFood      |
+------------------------------+-------------------------------+
                               |
+------------------------------v-------------------------------+
|  Persistence layer    FileManager  ->  inventory_data.txt    |
+--------------------------------------------------------------+
```

Exceptions: `InvalidFoodInputException` (bad user input) and `InventoryStorageException` (file problems) travel up to `App`, which shows them in dialogs.

## 5. Class reference

### GUI layer

**`App`** (`extends JFrame`)
The main window. Layout comes from `App.form`; this class only wires behaviour.
- Constant `APP_TITLE` holds the project name shown in every window title.
- `showMenu()`, `showSupplierEstablishment()`, `showRecipientEstablishment()`, `showRecipientHome()`, `openDashboard()` navigate between windows through `display(panel, title, size)`.
- `initDonorPortal()`, `initDispatchFeed()`, `initActiveDeliveries()`, `initRecipientSection()` attach the button and dropdown listeners for each part.
- `refreshTables()` reloads the dispatch feed and active deliveries tables from `DispatchManager`.
- `guard(GuardedAction)` runs a button action and turns `InvalidFoodInputException` and `InventoryStorageException` into `JOptionPane` error dialogs.
- `main` sets the system look and feel and starts the app on the Swing event thread. If the saved file cannot be read at startup, it shows a "Storage Error" dialog and exits.

**`App.form`**
The IntelliJ GUI Designer layout bound to `App`. The root panel is `pnlMain`, which holds `selectWindow`, `supplierWindow`, `recipientWindow`, `mainRecipientWindow` and `dashboardWindow`. Do not rename a component here without renaming its field in `App.java`.

**`PanelManager`**
Keeps a list of `JPanel` windows and makes sure only one is visible.
- `add(JPanel)` registers a window.
- `show(JPanel)` hides all registered windows, then shows the chosen one. Throws `IllegalArgumentException` if the panel was never registered.
- `hideAll()` hides everything.
- `getCurrent()` returns the visible window.

### Logic layer

**`DispatchManager`** (`final`)
The single entry point for business logic (all GUI actions go through it).
- `addFoodItem(perishable, name, quantityText, expiryText, flag, donor)` validates input, creates the right `FoodItem` with the next batch id (`B001`, `B002`, ...), and saves. Throws `InvalidFoodInputException`.
- `setDispatchStrategy(DispatchStrategy)` changes how the feed is sorted.
- `processMatching()` returns batches that are `AVAILABLE` and safe, sorted by the current strategy.
- `claim(id, recipient)` marks an available batch `CLAIMED`.
- `getActiveDeliveries()` returns `CLAIMED` and `ON ROUTE` batches.
- `assignVolunteer(id, volunteer)` validates the name and moves a `CLAIMED` batch to `ON ROUTE`.
- `markDelivered(id)` moves an `ON ROUTE` batch to `DELIVERED`.
- `save()` writes the whole inventory through `FileManager`.
- On first run, there is no data file yet, so it is empty.

**`DispatchStrategy`** (interface)
`List<FoodItem> sortAndMatch(List<FoodItem> items)`: contract for ordering the feed.

**`UrgencyFirstStrategy`** implements `DispatchStrategy`: shortest time before expiry first.

**`VolumeFirstStrategy`** implements `DispatchStrategy`: highest quantity first.

### Model layer

**`FoodItem`** (abstract)
Common state of a food batch: `batchId`, `name`, `donorName`, `quantity`, `status`, `claimedBy`, `volunteer`. Status constants: `AVAILABLE`, `CLAIMED`, `ON_ROUTE`, `DELIVERED`. Abstract methods `isSafeForDistribution()`, `getHoursLeft()`, `getTypeName()`, `extraCsv()`. `toCsv()` produces one line for the data file.

**`PerishableFood`** extends `FoodItem`
Has `hoursUntilExpiration` and `requiresRefrigeration`. **Safe when more than 2 hours remain.**

**`NonPerishableFood`** extends `FoodItem`
Has `daysUntilBestBefore` and `packagingIntact`. **Safe when best-before is in the future and packaging is intact.** `getHoursLeft()` returns days x 24 so both types can be compared.

### Persistence layer

**`FileManager`** (utility class)
- `saveInventory(List<FoodItem>)` writes every batch as one CSV line (UTF-8).
- `loadInventory()` reads them back. Blank or malformed lines are skipped so one bad line cannot stop the app.
- `hasSavedData()` tells whether the data file already exists.
- Throws `InventoryStorageException` when the file cannot be written or read.

### Exceptions

| Class | Type | Used for |
| --- | --- | --- |
| `InvalidFoodInputException` | checked (`extends Exception`) | wrong or missing user input |
| `InventoryStorageException` | unchecked (`extends RuntimeException`) | save and load failures |

## 6. OOP concepts used

| Concept | Where it appears |
| --- | --- |
| **Abstraction** | `FoodItem` defines what every batch can do without saying how. |
| **Inheritance** | `PerishableFood` and `NonPerishableFood` extend `FoodItem`. |
| **Polymorphism** | `isSafeForDistribution()`, `getHoursLeft()` and `getTypeName()` behave differently per food type; `DispatchStrategy` variables hold either strategy. |
| **Encapsulation** | Private fields with getters and setters in `FoodItem`; GUI components are private fields of `App`. |
| **Interface** | `DispatchStrategy`. |
| **Strategy pattern** | `UrgencyFirstStrategy` and `VolumeFirstStrategy` are swapped at runtime from the dropdown. |
| **Facade (informal)** | `DispatchManager` hides inventory, rules and file handling behind a few simple methods. |
| **Custom exceptions** | `InvalidFoodInputException`, `InventoryStorageException`. |
| **File handling** | `FileManager` with `java.nio.file`. |
| **Event handling** | Lambda `ActionListener`s on every button and dropdown. |

## 7. Validation and error handling

All errors are shown with `JOptionPane` so the user never sees a stack trace.

| Situation | Dialog title | Message |
| --- | --- | --- |
| Food name blank | Invalid Input | Food name cannot be left blank! |
| Food name has a comma | Invalid Input | Food name cannot contain commas! |
| Quantity or expiry is not a whole number | Invalid Input | Quantity and expiry must be whole numbers! |
| Quantity is 0 or negative | Invalid Input | Quantity must be greater than zero! |
| Expiry is 0 or negative | Invalid Input | Expiration value must be greater than zero! |
| Volunteer name blank | Invalid Input | Volunteer name cannot be blank! |
| Volunteer name has a comma | Invalid Input | Volunteer name cannot contain commas! |
| Claim with no row selected | No Selection | Select an available batch first. |
| Assign volunteer on a row that is not CLAIMED | No Selection | Select a CLAIMED batch first. |
| Mark delivered on a row that is not ON ROUTE | No Selection | Select an ON ROUTE delivery. |
| Recipient claim with nothing checked | No Item Selected | Please check at least one food item to claim. |
| File cannot be saved or read | Storage Error | Describes the file and the problem. |

Commas are rejected in names because the data file is comma separated.

## 8. Data storage

The inventory is saved to `inventory_data.txt` in the folder the app is run from, after every change. One line per batch, 10 comma-separated fields:

```
type,batchId,name,quantity,donor,expiry,flag,status,claimedBy,volunteer
```

Example:

```
Perishable,B001,Bread rolls,40,Sunrise Bakery,6,false,AVAILABLE,-,-
Non-perishable,B002,Canned sardines,120,Metro Supermart,30,true,CLAIMED,supplier1 (Bakery),-
```

- `expiry` is hours (perishable) or days (non-perishable).
- `flag` is *requires refrigeration* (perishable) or *packaging intact* (non-perishable).
- `-` means "not set yet".
- Delete the file to reset the app.

## 9. Project structure

```
DonatEAT/
|-- README.md
|-- .gitignore
`-- src/
    `-- Swing/
        |-- App.java
        |-- App.form
        |-- PanelManager.java
        |-- DispatchManager.java
        |-- DispatchStrategy.java
        |-- UrgencyFirstStrategy.java
        |-- VolumeFirstStrategy.java
        |-- FoodItem.java
        |-- PerishableFood.java
        |-- NonPerishableFood.java
        |-- FileManager.java
        |-- InvalidFoodInputException.java
        `-- InventoryStorageException.java
```

## 10. Getting started

1. Install **JDK 17 or newer** and **IntelliJ IDEA**.
2. Clone the repository: **File > New > Project from Version Control**, paste the repository URL.
3. Check **File > Project Structure > Project**: SDK is 17 or newer.
4. Check **Settings > Build, Execution, Deployment > Compiler > User Interface Designer** is enabled, then **Build > Rebuild Project**.
5. Open `src/Swing/App.java` and click the green run arrow next to `main`.

The first run creates an empty `inventory_data.txt`.

## 11. Run to Check our App

1. Menu: Supplier opens the establishment screen, Back returns.
2. Confirm opens the dashboard; the title reads `DonatEAT - Dashboard (supplier1 | Supplier | <establishment>)`.
3. Add Surplus with empty or invalid fields shows the matching error dialog from section 7.
4. Add a valid perishable and a valid non-perishable item; both appear in the feed.
5. Switch the strategy dropdown; the order changes.
6. Claim with no selection warns; claiming a selected row moves it to Active Deliveries.
7. Assign Volunteer with a blank name shows an error; a valid name changes the status to ON ROUTE.
8. Mark Delivered removes the row from Active Deliveries.
9. Back to Menu works; restarting the app keeps the data.

## 12. Known limitations and roadmap

*pending*