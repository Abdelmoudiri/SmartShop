# Documentation du Système de Gestion des Rôles et Autorisations

## Vue d'ensemble

SmartShop implémente un système de gestion des autorisations basé sur deux rôles principaux : **ADMIN** et **CLIENT**. Ce système garantit que chaque utilisateur n'accède qu'aux ressources pour lesquelles il a les permissions nécessaires.

## Architecture du Système d'Autorisation

### Composants Clés

1. **SecurityUtil** (`util/SecurityUtil.java`)
   - Classe utilitaire statique pour récupérer l'utilisateur connecté
   - Méthodes de vérification des rôles
   - Validation des permissions (lance des exceptions si non autorisé)

2. **AuthorizationService** (`services/AuthorizationService.java`)
   - Service centralisé pour les vérifications de permissions métier
   - Logique d'accès aux ressources (clients, commandes, paiements)
   - Gestion des permissions contextuelles

3. **AuthInterceptor** (`interceptor/AuthInterceptor.java`)
   - Intercepteur HTTP pour valider l'authentification
   - Vérifie la session active
   - Les autorisations sont vérifiées dans les contrôleurs

## Rôles Disponibles

### ADMIN
- **Permissions complètes** sur toutes les ressources
- Peut créer, modifier, supprimer clients, produits, commandes, paiements
- Peut consulter toutes les données de l'application
- Peut effectuer des opérations sensibles (encaisser/rejeter paiements, confirmer/annuler commandes)

### CLIENT
- **Permissions limitées** aux propres données
- Peut consulter son propre profil et ses commandes
- Peut consulter le catalogue produits (lecture seule)
- **Ne peut pas** créer de commandes (réservé aux ADMIN pour validation manuelle)
- **Ne peut pas** effectuer d'opérations administratives

## Matrice des Permissions

| Ressource | Endpoint | Méthode HTTP | ADMIN | CLIENT | Public |
|-----------|----------|--------------|-------|--------|--------|
| **Authentification** |
| Login | `/api/auth/login` | POST | ✅ | ✅ | ✅ |
| Register | `/api/auth/register` | POST | ✅ | ✅ | ✅ |
| Logout | `/api/auth/logout` | POST | ✅ | ✅ | ✅ |
| **Clients** |
| Créer client | `/api/clients` | POST | ✅ | ❌ | ❌ |
| Liste clients | `/api/clients` | GET | ✅ | ❌ | ❌ |
| Détail client | `/api/clients/{id}` | GET | ✅ | ✅* | ❌ |
| Modifier client | `/api/clients/{id}` | PUT | ✅ | ❌ | ❌ |
| Supprimer client | `/api/clients/{id}` | DELETE | ✅ | ❌ | ❌ |
| Commandes client | `/api/clients/{id}/orders` | GET | ✅ | ✅* | ❌ |
| **Produits** |
| Créer produit | `/api/products` | POST | ✅ | ❌ | ❌ |
| Liste produits | `/api/products` | GET | ✅ | ✅ | ✅ |
| Détail produit | `/api/products/{id}` | GET | ✅ | ✅ | ✅ |
| Recherche produits | `/api/products/search` | GET | ✅ | ✅ | ✅ |
| Modifier produit | `/api/products/{id}` | PUT | ✅ | ❌ | ❌ |
| Supprimer produit | `/api/products/{id}` | DELETE | ✅ | ❌ | ❌ |
| **Commandes** |
| Créer commande | `/api/orders` | POST | ✅ | ❌ | ❌ |
| Liste commandes | `/api/orders` | GET | ✅ | ❌ | ❌ |
| Détail commande | `/api/orders/{id}` | GET | ✅ | ✅* | ❌ |
| Confirmer commande | `/api/orders/{id}/confirm` | POST | ✅ | ❌ | ❌ |
| Annuler commande | `/api/orders/{id}/cancel` | POST | ✅ | ❌ | ❌ |
| Rejeter commande | `/api/orders/{id}/reject` | POST | ✅ | ❌ | ❌ |
| **Paiements** |
| Créer paiement | `/api/payments` | POST | ✅ | ❌ | ❌ |
| Liste paiements | `/api/payments` | GET | ✅ | ❌ | ❌ |
| Détail paiement | `/api/payments/{id}` | GET | ✅ | ❌ | ❌ |
| Encaisser paiement | `/api/payments/{id}/encash` | POST | ✅ | ❌ | ❌ |
| Rejeter paiement | `/api/payments/{id}/reject` | POST | ✅ | ❌ | ❌ |
| Paiements commande | `/api/orders/{orderId}/payments` | GET | ✅ | ✅* | ❌ |

**Légende** :
- ✅ = Autorisé
- ❌ = Refusé
- ✅* = Autorisé uniquement pour ses propres données

## Utilisation dans les Contrôleurs

### 1. Utilisation de SecurityUtil (vérifications simples)

```java
@PostMapping
public ResponseEntity<ClientDTO.Response> createClient(@RequestBody ClientDTO.CreateRequest request) {
    // Vérifier que l'utilisateur est admin
    SecurityUtil.requireAdmin();
    
    // Suite du traitement...
    ClientDTO.Response client = clientService.createClient(request);
    return ResponseEntity.ok(client);
}
```

### 2. Utilisation d'AuthorizationService (vérifications contextuelles - RECOMMANDÉ)

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final AuthorizationService authorizationService;
    private final OrderService orderService;
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO.Response> getOrder(@PathVariable Long id) {
        // Vérifier l'accès à la commande (ADMIN ou propriétaire)
        authorizationService.checkOrderAccess(id);
        
        OrderDTO.Response order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
}
```

## Méthodes Disponibles

### SecurityUtil (statiques)

```java
// Récupération utilisateur
User getCurrentUser()
Long getCurrentUserId()
UserRole getCurrentUserRole()

// Vérifications booléennes
boolean isAdmin()
boolean isClient()
boolean hasAnyRole(UserRole... roles)
boolean isAdminOrOwner(Long resourceOwnerId)

// Validations (lancent BusinessException si échec)
void requireRole(UserRole role)
void requireAnyRole(UserRole... roles)
void requireAdmin()
void requireAdminOrOwner(Long resourceOwnerId)
```

### AuthorizationService (injectées)

```java
// Vérifications d'accès aux ressources
void checkClientAccess(Long clientId)
void checkOrderAccess(Long orderId)
void checkClientOrdersAccess(Long clientId)
void checkOrderPaymentsAccess(Long orderId)

// Vérifications d'opérations
void checkOrderModificationAccess()
void checkPaymentOperationAccess()
void requireAdmin()
void requireAnyRole(UserRole... roles)

// Récupération contexte
Client getCurrentClient() // Pour les utilisateurs CLIENT
```

## Gestion des Erreurs

### Erreurs 401 Unauthorized
- Aucune session active
- Utilisateur non connecté
- **Message** : "Accès refusé. Veuillez vous connecter."

### Erreurs 403 Forbidden
- Utilisateur connecté mais sans les permissions requises
- Tentative d'accès aux données d'un autre utilisateur (pour CLIENT)
- **Message** : "Accès refusé. Rôles autorisés : [ADMIN]" ou "Vous ne pouvez accéder qu'à vos propres données."

### Erreurs 422 Unprocessable Entity (BusinessException)
- Règle métier violée (via SecurityUtil ou AuthorizationService)
- **Exemple** : "L'utilisateur actuel n'est pas un client"

## Flux d'Autorisation

```
1. Requête HTTP arrive → AuthInterceptor
   ↓
2. Vérifier si endpoint public (auth, swagger, produits en GET)
   ↓ (NON)
3. Vérifier session active et utilisateur connecté
   ↓ (OUI)
4. Vérifier annotation @RequiresRole sur méthode/classe
   ↓ (SI PRÉSENTE)
5. Comparer rôle utilisateur avec rôles autorisés
   ↓ (SI MATCH)
6. Requête passe au contrôleur
   ↓
7. Contrôleur peut faire vérifications supplémentaires via AuthorizationService
   ↓ (SI AUTORISÉ)
8. Traitement métier et réponse
```

## Exemples de Scénarios

### Scénario 1 : CLIENT consulte son profil
```
GET /api/clients/5
Headers: Cookie avec JSESSIONID

Processus :
1. AuthInterceptor : Session valide ✅
2. ClientController.getClient(5) : Pas d'annotation @RequiresRole sur méthode
3. authorizationService.checkClientAccess(5) :
   - Utilisateur = CLIENT (userId=10)
   - Client 5 → userId=10 ✅ Match
4. Retour profil client ✅
```

### Scénario 2 : CLIENT tente de consulter un autre profil
```
GET /api/clients/8
Headers: Cookie avec JSESSIONID

Processus :
1. AuthInterceptor : Session valide ✅
2. ClientController.getClient(8)
3. authorizationService.checkClientAccess(8) :
   - Utilisateur = CLIENT (userId=10)
   - Client 8 → userId=15 ❌ Pas de match
4. BusinessException: "Accès refusé. Vous ne pouvez accéder qu'à votre propre profil."
5. HTTP 422 Unprocessable Entity ❌
```

### Scénario 3 : CLIENT tente de créer une commande
```
POST /api/orders
Headers: Cookie avec JSESSIONID

Processus :
1. AuthInterceptor : Session valide ✅
2. @RequiresRole(UserRole.ADMIN) sur OrderController
3. Utilisateur = CLIENT ❌
4. HTTP 403 Forbidden : "Accès refusé. Rôles autorisés : [ADMIN]" ❌
```

### Scénario 4 : ADMIN consulte n'importe quel profil
```
GET /api/clients/8
Headers: Cookie avec JSESSIONID

Processus :
1. AuthInterceptor : Session valide ✅
2. ClientController.getClient(8)
3. authorizationService.checkClientAccess(8) :
   - Utilisateur = ADMIN ✅ Bypass automatique
4. Retour profil client 8 ✅
```

## Bonnes Pratiques

### ✅ À FAIRE

1. **Utiliser SecurityUtil.requireAdmin() pour les opérations ADMIN uniquement**
   ```java
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
       SecurityUtil.requireAdmin();
       productService.deleteProduct(id);
       return ResponseEntity.noContent().build();
   }
   ```

2. **Utiliser AuthorizationService pour la logique contextuelle (RECOMMANDÉ)**
   ```java
   @GetMapping("/{id}")
   public ResponseEntity<OrderDTO.Response> getOrder(@PathVariable Long id) {
       authorizationService.checkOrderAccess(id); // Admin OU propriétaire
       OrderDTO.Response order = orderService.getOrderById(id);
       return ResponseEntity.ok(order);
   }
   ```

3. **Toujours vérifier les permissions AVANT les opérations métier**
   ```java
   @PostMapping("/{id}/confirm")
   public ResponseEntity<OrderDTO.Response> confirmOrder(@PathVariable Long id) {
       authorizationService.requireAdmin();
       OrderDTO.Response order = orderService.confirmOrder(id);
       return ResponseEntity.ok(order);
   }
   ```

4. **Utiliser SecurityUtil.getCurrentUser() pour obtenir le contexte**
   ```java
   User currentUser = SecurityUtil.getCurrentUser();
   log.info("Opération effectuée par : {}", currentUser.getUsername());
   ```

### ❌ À ÉVITER

1. **Ne pas vérifier les permissions dans les Services**
   - Les vérifications doivent être dans les Contrôleurs
   - Les Services doivent rester agnostiques de la sécurité

2. **Ne pas hard-coder les vérifications partout**
   - Utiliser AuthorizationService pour centraliser la logique

3. **Ne pas ignorer les erreurs d'autorisation**
   - Laisser les exceptions BusinessException remonter
   - GlobalExceptionHandler les formatera correctement

4. **Ne pas mélanger authentification et autorisation**
   - AuthInterceptor = Authentification (qui es-tu ?)
   - SecurityUtil + AuthorizationService = Autorisation (que peux-tu faire ?)

## Configuration Requise

### 1. WebConfig doit enregistrer l'intercepteur
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }
}
```

### 2. Session HTTP doit être configurée
```properties
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
```

## Tests d'Autorisation

### Test avec Postman

1. **Login ADMIN**
   ```
   POST /api/auth/login
   {
     "username": "admin",
     "password": "admin123"
   }
   → Copier JSESSIONID du cookie
   ```

2. **Tester accès ADMIN**
   ```
   GET /api/clients
   Headers: Cookie: JSESSIONID={valeur}
   → HTTP 200 ✅
   ```

3. **Login CLIENT**
   ```
   POST /api/auth/login
   {
     "username": "client1",
     "password": "client123"
   }
   → Copier nouveau JSESSIONID
   ```

4. **Tester restriction CLIENT**
   ```
   GET /api/clients
   Headers: Cookie: JSESSIONID={valeur_client}
   → HTTP 403 Forbidden ✅
   ```

5. **Tester accès propre profil CLIENT**
   ```
   GET /api/clients/5 (si client1 → clientId=5)
   Headers: Cookie: JSESSIONID={valeur_client}
   → HTTP 200 ✅
   ```

## Conclusion

Le système d'autorisation de SmartShop offre :
- ✅ **Sécurité granulaire** : Contrôle précis des permissions par rôle
- ✅ **Flexibilité** : Annotations + Services pour tous les cas d'usage
- ✅ **Clarté** : Matrice de permissions documentée
- ✅ **Maintenabilité** : Logique centralisée dans AuthorizationService
- ✅ **Testabilité** : Facile à tester via Postman avec différents rôles

Pour toute question ou ajout de permissions, modifier :
1. `AuthorizationService` pour la logique métier
2. `AuthInterceptor` pour les règles globales
3. Cette documentation pour tracer les changements
