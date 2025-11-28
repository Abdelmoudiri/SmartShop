# SmartShop - Application de Gestion Commerciale B2B

## 📋 Vue d'ensemble

SmartShop est une API REST backend pour la gestion commerciale de **MicroTech Maroc**, distributeur de matériel informatique. L'application gère 650 clients actifs avec un système de fidélité progressif et des paiements fractionnés multi-moyens.

## 🛠️ Technologies utilisées

- **Backend**: Spring Boot 4.0.0
- **Java**: 17
- **Base de données**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Validation
- **Build**: Maven
- **Conteneurisation**: Docker & Docker Compose
- **Tests**: JUnit, Mockito

## 🏗️ Architecture

```
SmartShop/
├── controller/      # API REST Endpoints
├── services/        # Logique métier
├── repositories/    # Accès données (JPA)
├── entities/        # Modèle de données
├── dto/             # Data Transfer Objects
├── mapper/          # Conversion Entity <-> DTO (MapStruct)
├── exceptions/      # Gestion centralisée des erreurs
├── config/          # Configuration Spring
├── interceptor/     # Authentification HTTP Session
└── util/            # Utilitaires (hashage mot de passe)
```

## 📊 Modèle de données

### Entités principales

- **User**: Authentification (ADMIN/CLIENT)
- **Client**: Informations client + statistiques fidélité
- **Product**: Catalogue produits avec soft delete
- **Order**: Commandes avec calculs automatiques
- **OrderItem**: Lignes de commande
- **Payment**: Paiements multi-moyens
- **PromoCode**: Codes promotionnels

### Enums

- `UserRole`: ADMIN, CLIENT
- `CustomerTier`: BASIC, SILVER, GOLD, PLATINUM
- `OrderStatus`: PENDING, CONFIRMED, CANCELED, REJECTED
- `PaymentStatus`: EN_ATTENTE, ENCAISSE, REJETE
- `PaymentType`: ESPECES, CHEQUE, VIREMENT

## 🎯 Fonctionnalités implémentées

### ✅ 1. Gestion des Clients (ClientService)

- ✅ Créer/consulter/modifier/supprimer des clients
- ✅ Statistiques automatiques: `totalOrders`, `totalSpent`
- ✅ Dates `firstOrderDate` et `lastOrderDate`
- ✅ Calcul automatique du niveau de fidélité:
  - **BASIC**: Par défaut (0 commande)
  - **SILVER**: 3 commandes OU 1,000 DH
  - **GOLD**: 10 commandes OU 5,000 DH
  - **PLATINUM**: 20 commandes OU 15,000 DH

### ✅ 2. Système de Fidélité Automatique

- ✅ Mise à jour du niveau après chaque commande CONFIRMED
- ✅ Application des remises selon le niveau actuel:
  - **SILVER**: 5% si sous-total ≥ 500 DH
  - **GOLD**: 10% si sous-total ≥ 800 DH
  - **PLATINUM**: 15% si sous-total ≥ 1200 DH

### ✅ 3. Gestion des Produits (ProductService)

- ✅ CRUD complet avec pagination
- ✅ Recherche par nom
- ✅ Soft delete (marquage comme supprimé si utilisé dans des commandes)
- ✅ Validation et réservation du stock
- ✅ Restauration du stock si commande annulée

### ✅ 4. Gestion des Commandes (OrderService)

#### Création de commande avec calculs automatiques:

```
1. Sous-total HT = Σ (prix unitaire × quantité)
2. Remise fidélité selon niveau client
3. Remise code promo (+5% si valide: PROMO-XXXX)
4. Montant après remise = Sous-total - Remises
5. TVA 20% = Montant après remise × 0.20
6. Total TTC = Montant après remise + TVA
```

#### Gestion des statuts:

- **PENDING**: En attente de validation (par défaut)
- **CONFIRMED**: Validée par ADMIN (après paiement complet)
- **CANCELED**: Annulée par ADMIN (restaure le stock)
- **REJECTED**: Refusée (stock insuffisant)

#### Règles métier:

- ✅ Validation stock avant création
- ✅ Décrément automatique du stock
- ✅ Blocage CONFIRMED si `montantRestant > 0`
- ✅ Mise à jour automatique des statistiques client

### ✅ 5. Système de Paiements Multi-Moyens (PaymentService)

#### Types de paiement supportés:

| Type | Limite | Statut initial | Informations requises |
|------|--------|----------------|----------------------|
| **ESPECES** | 20,000 DH (Art. 193 CGI) | ENCAISSE | Montant uniquement |
| **CHEQUE** | Aucune | EN_ATTENTE | Numéro + Banque + Échéance |
| **VIREMENT** | Aucune | EN_ATTENTE | Référence + Banque |

#### Fonctionnalités:

- ✅ Paiement fractionné (plusieurs paiements pour une commande)
- ✅ Numéro séquentiel automatique: `PAY-ORD1-01`, `PAY-ORD1-02`...
- ✅ Validation limite 20k DH pour espèces
- ✅ Mise à jour automatique du `montantRestant`
- ✅ Encaissement manuel par ADMIN (chèque/virement)
- ✅ Rejet de paiement (chèque sans provision)

### ✅ 6. Authentification HTTP Session

- ✅ Login/Logout sans JWT ni Spring Security
- ✅ Session simple avec `HttpSession`
- ✅ Intercepteur pour vérifier l'authentification
- ✅ Rôles: ADMIN (gestion complète), CLIENT (consultation uniquement)

### ✅ 7. Gestion Centralisée des Erreurs

- ✅ `@ControllerAdvice` avec `GlobalExceptionHandler`
- ✅ Codes HTTP cohérents (400, 401, 403, 404, 422, 500)
- ✅ Réponses JSON standardisées avec timestamp, message, chemin

## 🚀 Installation et lancement

### Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0 (si exécution locale sans Docker)

### Démarrage avec Docker (Recommandé)

```bash
# Cloner le projet
git clone https://github.com/votre-username/SmartShop.git
cd SmartShop

# Lancer avec Docker Compose
docker-compose up -d --build

# Vérifier les logs
docker logs smartshop-backend -f

# L'application sera disponible sur http://localhost:8081
```

### Démarrage local (MySQL en Docker)

```bash
# Démarrer uniquement MySQL
docker-compose up -d mysqldb

# Lancer le backend localement
./mvnw spring-boot:run

# L'application sera disponible sur http://localhost:8080
```

## 📡 API Endpoints

### Authentification

```http
POST /api/auth/register - Créer un compte
POST /api/auth/login    - Se connecter
POST /api/auth/logout   - Se déconnecter
GET  /api/auth/me       - Obtenir l'utilisateur connecté
GET  /api/auth/health   - Vérifier l'état de l'application
```

### Clients (ADMIN uniquement pour CRUD)

```http
POST   /api/clients           - Créer un client
GET    /api/clients           - Liste tous les clients
GET    /api/clients/{id}      - Détails d'un client
PUT    /api/clients/{id}      - Modifier un client
DELETE /api/clients/{id}      - Supprimer un client
GET    /api/clients/{id}/orders - Historique des commandes
```

### Produits

```http
POST   /api/products          - Créer un produit (ADMIN)
GET    /api/products          - Liste avec pagination
GET    /api/products/{id}     - Détails d'un produit
PUT    /api/products/{id}     - Modifier (ADMIN)
DELETE /api/products/{id}     - Supprimer (soft delete, ADMIN)
GET    /api/products/search?nom={nom} - Rechercher par nom
```

### Commandes

```http
POST   /api/orders            - Créer une commande (ADMIN)
GET    /api/orders            - Liste toutes les commandes
GET    /api/orders/{id}       - Détails d'une commande
POST   /api/orders/{id}/confirm  - Valider (ADMIN, paiement complet requis)
POST   /api/orders/{id}/cancel   - Annuler (ADMIN, restaure stock)
GET    /api/clients/{clientId}/orders - Commandes d'un client
```

### Paiements

```http
POST   /api/payments          - Créer un paiement (ADMIN)
GET    /api/payments          - Liste tous les paiements
GET    /api/payments/{id}     - Détails d'un paiement
POST   /api/payments/{id}/encash  - Encaisser (ADMIN, chèque/virement)
POST   /api/payments/{id}/reject  - Rejeter (ADMIN)
GET    /api/orders/{orderId}/payments - Paiements d'une commande
```

## 🧪 Exemple de flux complet

### 1. Créer un client

```json
POST /api/clients
{
  "nom": "TechCorp Casablanca",
  "email": "contact@techcorp.ma"
}
```

### 2. Créer une commande

```json
POST /api/orders
{
  "clientId": 1,
  "items": [
    {"productId": 5, "quantite": 2},
    {"productId": 12, "quantite": 1}
  ],
  "codePromo": "PROMO-X9Y2"
}
```

**Calculs automatiques:**
- Sous-total: 1,500 DH
- Remise fidélité (SILVER): 5% = -75 DH
- Remise promo: 5% = -75 DH
- Montant après remise: 1,350 DH
- TVA 20%: 270 DH
- **Total TTC: 1,620 DH**

### 3. Payer en 3 fois

```json
// Paiement 1 - Espèces
POST /api/payments
{
  "orderId": 1,
  "montant": 1000,
  "typePaiement": "ESPECES"
}
// → Statut: ENCAISSE immédiatement
// → montantRestant: 620 DH

// Paiement 2 - Chèque
POST /api/payments
{
  "orderId": 1,
  "montant": 500,
  "typePaiement": "CHEQUE",
  "reference": "CHQ-12345678",
  "banque": "BMCE Bank",
  "dateEcheance": "2025-12-15"
}
// → Statut: EN_ATTENTE
// → montantRestant: 620 DH (pas encore encaissé)

// ADMIN encaisse le chèque
POST /api/payments/2/encash
// → Statut: ENCAISSE
// → montantRestant: 120 DH

// Paiement 3 - Virement
POST /api/payments
{
  "orderId": 1,
  "montant": 120,
  "typePaiement": "VIREMENT",
  "reference": "VIR-2025-11-27-4521",
  "banque": "Attijariwafa Bank"
}
// → montantRestant: 0 DH
```

### 4. Valider la commande

```json
POST /api/orders/1/confirm
// ✅ Commande passée à CONFIRMED
// ✅ Client: totalOrders +1, totalSpent +1620 DH
// ✅ Niveau recalculé automatiquement
```

## 📈 Règles métier critiques

### Validation stock
✅ `quantité_demandée ≤ stock_disponible`

### Arrondis
✅ Tous les montants à 2 décimales

### Codes promo
✅ Format strict `PROMO-XXXX` (`@Pattern` validation)
✅ Usage unique possible

### TVA
✅ 20% par défaut (configurable via `smartshop.tva.rate`)
✅ Appliquée APRÈS les remises (standard marocain)

### Paiement
✅ Espèces limité à 20,000 DH (Art. 193 CGI)
✅ Commande CONFIRMED uniquement si `montantRestant = 0`

## 🔒 Sécurité et Permissions

### ADMIN peut:
- ✅ Toutes les opérations CRUD
- ✅ Créer des commandes pour n'importe quel client
- ✅ Valider/Annuler/Rejeter des commandes
- ✅ Enregistrer et encaisser des paiements

### CLIENT peut:
- ✅ Se connecter
- ✅ Consulter SES PROPRES données (profil, commandes, statistiques)
- ✅ Consulter la liste des produits (lecture seule)
- ❌ Créer/Modifier/Supprimer quoi que ce soit
- ❌ Voir les données des autres clients

## 🐛 Gestion des erreurs

### Codes HTTP

| Code | Signification | Exemple |
|------|---------------|---------|
| 400 | Bad Request | Validation échouée |
| 401 | Unauthorized | Non authentifié |
| 403 | Forbidden | Permissions insuffisantes |
| 404 | Not Found | Ressource inexistante |
| 422 | Unprocessable Entity | Règle métier violée (stock insuffisant, commande déjà validée) |
| 500 | Internal Server Error | Erreur interne |

### Format de réponse d'erreur

```json
{
  "timestamp": "2025-11-27T14:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Stock insuffisant pour le produit 'Laptop HP'. Disponible: 5, Demandé: 10",
  "path": "/api/orders"
}
```

## 🧪 Tests

```bash
# Exécuter tous les tests
./mvnw test

# Tests avec couverture
./mvnw test jacoco:report
```

## 📝 Configuration

### application.properties

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/smartshop_db
spring.datasource.username=root
spring.datasource.password=root

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Taux de TVA (20% par défaut)
smartshop.tva.rate=0.20
```

## 👤 Auteur

Développé par **[Votre Nom]** pour MicroTech Maroc

## 📅 Dates

- **Lancement**: 24/11/2025
- **Livraison**: 28/11/2025
- **Durée**: 5 jours

## 📄 Licence

Projet pédagogique - YouCode Maroc
