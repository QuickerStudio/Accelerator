#!/bin/bash

# Rewrite commit messages to English
git filter-branch -f --msg-filter '
  case "$GIT_COMMIT" in
    a32ab5c9a39e1688e34387390433da8c7159bb99)
      echo "Add HistoryManager for conversation history management

- Save and load conversation history
- Manage history by session
- Query and export history functionality
- Auto-limit messages (max 1000 per session)
- Reactive state updates with StateFlow
- Integrate ConfigManager for persistence"
      ;;
    e5532ffed5eca9d9890398f39d4449a6a4bb26f8)
      echo "Add ConversationHistory data model

- Create ConversationHistory.kt data model
- Include session ID, message list, timestamps
- Provide methods to add messages, get last N messages, clear history
- Foundation for history manager"
      ;;
    a88f96c9bbc5ebf963cebc7faff807cbfff80996)
      echo "Add SessionManager for session management

- Create, delete, switch sessions
- Session list management and persistence
- Track current active session
- Reactive state updates with StateFlow
- Integrate ConfigManager for persistence"
      ;;
    cff4135e5597b25fc923957c99af596386703f34)
      echo "Add Session data model for session management system

- Create Session.kt data model
- Include session ID, title, agent role, timestamps
- Provide session summary, update timestamp, increment message count methods
- Foundation for history management"
      ;;
    2ed58816d33150c7839f6a53fdb513ec15ce753b)
      echo "Fix model download card button logic

- Remove green checkmark button (logic error)
- Add 'Load Model' button for manual initialization
- Add 'Clear Model' button to delete downloaded model
- Download no longer auto-initializes, requires manual load
- Improve user control over model lifecycle"
      ;;
    efc6be8de1ecdd6a6d751d72814156eb7e5879c8)
      echo "Fix ConfigManager inline function compilation error

- Change inline reified functions to regular generic functions
- getObject now requires Class<T> parameter
- getList now requires Type parameter
- Set prefs and gson to internal access level"
      ;;
    e63c01573d810c27f08a96005a157ebee6261df9)
      echo "Fix ConfigManager inline function access permission issue"
      ;;
    08299eaf8a42ccce6d84aa6aa985069154ac0a19)
      echo "Build logging and configuration management infrastructure

- Create AppLogger: file-based persistent logging system
- Create ConfigManager: unified configuration management
- Create ModelConfig: model state persistence to solve 'amnesia' problem
- Update MainActivity: initialize infrastructure systems
- Update GemmaInferenceManager: integrate logging and config management
- Update ModelDownloadManager: use ModelConfig for persistent download state
- Add markInitializationSuccess/Failed methods to ModelConfig"
      ;;
    *)
      cat
      ;;
  esac
' HEAD~8..HEAD
