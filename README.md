# MyPuM
Punto de venta Android **offline-first**, Kotlin + Jetpack Compose + Room + Hilt + DataStore + WorkManager, preparado para CI/CD en GitHub Actions.

## Arquitectura
`feature (MVI) → usecase → domain repository → data repository → Room/Remote`.
La UI no conoce entidades Room ni DTOs.

## Compilar
```bash
./gradlew assembleDebug
./gradlew test
```
Requiere JDK 17 y Android SDK 34. El proyecto incluye Gradle Wrapper.
Firebase es opcional en runtime; añade `google-services.json` cuando se configure el backend real.

## Principios
- Una sola fuente local de verdad: Room.
- Sincronización eventual mediante `SyncQueueEntity` + WorkManager.
- Dinero con `BigDecimal`.
- Tiempo inyectable con `ClockProvider`.
- Dependencias invertidas mediante interfaces en `domain/repository`.
- Pantallas organizadas por feature y contratos MVI.
