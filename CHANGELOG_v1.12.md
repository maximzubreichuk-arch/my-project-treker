# v1.12 — Architecture cleanup

- Removed the `core/` package.
- Moved advertising UI to `presentation/ui/ads`.
- Moved advertising configuration/session persistence to `data/ads`.
- Moved audio implementation to `data/audio`.
- Moved task icon UI to `presentation/ui/taskicon`.
- Moved stable task icon identifiers to `domain/model`.
- Moved platform-independent task icon suggestion logic to `domain/service`.
- Moved locale context handling to `presentation/localization`.
- Moved application settings models to `domain/settings`.
- Moved Billing and subscription state implementation to `data/subscription`.
- Removed obsolete legacy unit tests from the project.
- Updated README to reflect the current package structure and architecture.
- Updated `.gitignore` for a clean Android source repository.
