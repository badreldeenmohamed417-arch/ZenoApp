# Zeno AI - Personalized EdTech Learning Companion 🎓🚀

Zeno is an AI-powered educational platform engineered to help students learn smarter, understand their curriculum, and get personalized academic assistance anytime. 

Instead of delivering generic responses like traditional chatbots, Zeno integrates a curriculum-aware **Retrieval-Augmented Generation (RAG)** pipeline to answer students based directly on their actual learning materials and textbooks.

> **Judges Notice:** This entire ecosystem—spanning a native Android application, an asynchronous FastAPI backend, database structures, and the bilingual RAG engine—was designed, coded, and architected **100% by a Solo Developer** within the tight timeline of the hackathon.

---

## Key Features & Product Experience

### The Deep Work Study Session (Core Value)
Zeno introduces a dedicated production environment designed to eliminate distractions and induce focus:
*   **Pomodoro Integration:** Built-in customized timers to structure focused study intervals and breaks.
*   **Ambient Focus Audio:** Integrated background sound selection (ambient layers) to enhance deep mental concentration.
*   **Instant Context-Aware Tutoring:** A floating Zeno icon resides at the bottom of the screen. At any moment during the study session, students can tap the icon to summon Zeno and ask questions related specifically to the curriculum materials loaded.

### Smart Features Overview
*   **Curriculum-Aware Answering:** Leverages RAG to query uploaded PDF learning documents.
*   **True Bilingual Engine:** Native support for Arabic (RTL) layouts alongside English (LTR) contexts without breaking the chat interfaces.
*   **Robust Security:** Secure email authentication, session control, and persistent conversation history.

---

## Complete Technical Architecture

Zeno is engineered using a robust, decoupled mobile-server architecture:

### Frontend (Android Application)
*   **Language & Platform:** Native `Kotlin` for maximum performance, fluid animations, and robust system resource handling.
*   **UI Architecture:** Jetpack Compose / XML layout adjustments meticulously optimized for fluid Right-to-Left (RTL) Arabic scripting and Left-to-Right (LTR) technical terms seamlessly combined in the same chat bubbles.
*   **Networking:** Modern REST API integration ensuring graceful handling of network delays or unexpected server dropouts.

### Backend & AI Infrastructure
*   **Framework:** `FastAPI` (Asynchronous Python backend) for fast real-time mobile transaction cycles.
*   **Database:** `PostgreSQL` backed by `SQLAlchemy` for robust session tracking, authentication logs, and chat histories.
*   **LLM Orchestration:** `Google Gemini API` combined with custom prompt boundaries.
*   **RAG Engine:** High-fidelity script processing to segment, index, and query text from multi-lingual educational PDF files.

---

## System Flow Chart

```text
[ Android App (Kotlin UI) ]  ───(Study Session & Audio)───┐
          │                                              │
          ▼ (Secure Token / JWT Auth)                    ▼
┌────────────────────────────────────────────────────────┐
│                   FastAPI Backend Server               │
│                                                        │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────┐  │
│  │  Auth Router │   │ Study Router │   │ Chat/RAG   │  │
│  └──────┬───────┘   └──────┬───────┘   └─────┬──────┘  │
└──       │                  │                 │      ───┘
          ▼                  ▼                 ▼
   ┌──────────────┐   ┌──────────────┐   ┌────────────┐
   │ PostgreSQL DB│   │ App Logic    │   │ Gemini LLM │
   │ (Users/Sess) │   │ (Pomodoro)   │   │  Vector DB │
   └──────────────┘   └──────────────┘   └────────────┘
```

---

## Important Note Regarding Demo & Deployment

**Zeno is currently a fully-functional localized prototype.** 
*   The Android mobile app is operational and fully coded.
*   The backend architecture is complete but runs on a local staging environment rather than a publicly deployed cloud production server. 
*   The submitted **Demo Video** demonstrates the real implemented UI, real product experience, and the underlying core functionality of Zeno using live screen recording. This is a deployment environment constraint of the hackathon prototype phase, not a structural limitation of the final software product design.

---

## How to Explore the Codebases

This project is separated into dedicated directories/repositories:
*   Frontend Mobile Code: Located inside this repository (`/android` or main project root).
*   Backend Server Code: Detailed codebase setup scripts, dependencies, database setup tables, and RAG architectures can be viewed at the backend section.

---

## Reflection & Horizons
Building Zeno as a solo developer taught me that building impactful AI products isn't just about calling an API endpoint; it is about building the system infrastructure around it. Synchronizing background threads, creating proper database schemes, stabilizing user sessions, and designing dual-language layouts required a complete end-to-end engineering mindset.

*Zeno succeeds in turning AI into an actual, integrated learning environment rather than just another generic chat window.*
