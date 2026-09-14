# Arquitectura
```text
UI Feature/MVI
    ↓
UseCases (reglas)
    ↓
Domain Repository interfaces
    ↓
Data Repository implementations
    ├── Room (offline source of truth)
    ├── DataStore (settings/session)
    └── Remote (Retrofit/Firebase)
    ↓
SyncQueue + WorkManager
```
Cada feature mantiene Screen/ViewModel/Contract y componentes propios.
