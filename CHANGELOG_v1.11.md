# MyProjectTreker v1.11 — Task Editor behavior fixes

## What changed

- Task title field no longer uses a fixed 55dp height; the Material text field can size itself correctly.
- Description field is multiline (2–4 lines) so entered text is not clipped.
- Repeat type is now selected from a single dropdown instead of six large radio tiles.
- Repeat labels remain: One-time, Daily, Weekly, Monthly, Yearly, Course (localized via existing resources).
- Weekly day selector is a single row of 7 equal cells with locale-aware short day names.
- Monthly day display is null-safe and falls back to the task date day.
- Additional times are shown and persisted only for COURSE.
- Existing non-COURSE tasks are sanitized at the data-mapping layer so legacy extra times are ignored.
- The Free COURSE extra-time explanation is no longer permanently displayed; it appears only when the user attempts to add an extra time beyond the Free limit.
- Save is moved to the Scaffold bottom bar and remains visible above system navigation and the IME/keyboard.
- Validation and ViewModel messages are shown via Snackbar instead of adding content below the form.
- Subtasks are collapsed by default in an expandable card, so a task with many subtasks does not force the user to scroll through the list just to find Save.
- Subtask editing uses the outer editor scroll only; no nested LazyColumn is used.
