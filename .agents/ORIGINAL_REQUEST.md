# Original User Request

## 2026-09-21T08:19:16Z

# Teamwork Project Prompt — Draft

> Status: Launched
> Goal: Craft prompt → get user approval → delegate to teamwork_preview
> Requested team: Small focused team

This is a single self-contained feature; keep it small and focused. Implement a centralized exception handling mechanism for UI actions in the Android application to prevent crashes, log real errors to Logcat, and show a generic Toast message to the user.

Working directory: /home/badr-eldeen/AndroidStudioProjects/Zeno
Integrity mode: development

## Requirements

### R1. Centralized Error Handling
Implement a global or base error handling approach (e.g., a `BaseViewModel` with a `CoroutineExceptionHandler`, or an extension function for `viewModelScope.launch`) across the application's ViewModels. It should catch unhandled exceptions during user actions.

### R2. Safe UI Feedback & Logging
When an error is caught:
1. Show a generic Toast message (e.g., "حدث خطأ غير متوقع" / "An unexpected error occurred").
2. Log the actual exception and stack trace to Android Logcat for debugging purposes.
3. Ensure no raw exception details or stack traces are ever displayed in the UI.

## Verification Resources
Create a temporary test action (e.g., a hidden button or temporary function) that explicitly raises a `RuntimeException` to verify that the app does not crash and the Toast is shown.

## Acceptance Criteria

### Error Handling
- [ ] Unhandled exceptions in ViewModels are caught centrally.
- [ ] The app does not crash when an action throws an error.

### UI & Logging
- [ ] A generic Toast is displayed to the user on error.
- [ ] Real exceptions are fully logged to Logcat.
- [ ] Raw error messages/stack traces are never shown to the user.

### Verification
- [ ] A programmatic or manual test triggering an intentional crash confirms the Toast appears and the app remains stable.
