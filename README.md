# JAN & CO Business Tax Calculator

A production-ready, lightweight, and responsive Android application developed for **JAN & CO Tax & Corporate Consultants** by **TEHSIN ULLAH JAN, Advocate High Court**.

## Branding & Identity
- **Business Name**: JAN & CO
- **Tagline**: Tax & Corporate Consultants
- **Lead Counsel**: TEHSIN ULLAH JAN, Advocate High Court
- **Design System**: Material Design 3 (M3) with a modern corporate palette featuring Deep Navy (`#0B1B3D`), Gold (`#D4AF37`), and Crisp White with dynamic Dark/Light theme support.

## Key Features
1. **Business Income Tax Calculator**:
   - Accurately calculates Pakistan Business Income Tax for Individuals using slab-based calculations according to Federal Board of Revenue (FBR) provisions.
   - Preloaded active Tax Year (2024-2025) slabs:
     - PKR 0 – 600,000: Tax = 0
     - PKR 600,001 – 1,200,000: 15% of amount exceeding 600,000
     - PKR 1,200,001 – 1,600,000: 90,000 + 20% exceeding 1,200,000
     - PKR 1,600,001 – 3,200,000: 170,000 + 30% exceeding 1,600,000
     - PKR 3,200,001 – 5,600,000: 650,000 + 40% exceeding 3,200,000
     - Above PKR 5,600,000: 1,610,000 + 45% exceeding 5,600,000

2. **Hidden Admin Panel**:
   - Password protected authentication (`janadmin`).
   - Full CRUD operations for Tax Years & Slabs.
   - Activate/Deactivate year enforcement (ensures only one active year is used automatically by the calculator).
   - View collected taxpayer lead details database.

3. **Local SQLite Persistence (Room)**:
   - `TaxYearEntity` & `TaxSlabEntity`: Dynamic storage for tax rates.
   - `CalculationHistoryEntity`: Local history with search, view, and deletion features.
   - `LeadEntity`: Local lead generation registry.

4. **PDF Assessment Report Export**:
   - Generates official PDF reports containing JAN & CO branding, Advocate Tehsin Ullah Jan credentials, tax breakdown tables, legal summary, and disclaimer.
   - Includes FileProvider integration for secure PDF sharing.

5. **Lead Generation**:
   - Collects taxpayer details (Name, Mobile, Email, City, Occupation, Purpose) before generating official PDF reports or submitting inquiries.

6. **WhatsApp Integration**:
   - Floating WhatsApp contact button (`+92 327 7669933`) with pre-filled message support.

7. **About & Chamber Directory**:
   - Complete chamber address placeholder, phone, email, and website details with direct call/email triggers.

8. **Settings & Preferences**:
   - Dark/Light mode toggle, regional currency formatting, rate app, share app, and privacy policy.

## Tech Architecture
- **Language**: Kotlin 2.2+
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Database**: Android Room + KSP (SQLite)
- **Architecture**: MVVM + Repository Pattern
- **Navigation**: Jetpack Navigation Compose
- **Document Engine**: Android `PdfDocument` & `FileProvider`
