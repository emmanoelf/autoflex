# Autoflex — Frontend

A responsive web interface for managing industrial inventory, built with **React + TypeScript** as part of a full-stack assessment project.

---

## Overview

This frontend consumes a REST API to manage products, raw materials, and their associations. It also displays a **production suggestion** — calculating which products can be manufactured based on current stock levels, prioritized by highest product value.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | React 18 + TypeScript |
| Build Tool | Vite |
| HTTP Client | Axios |
| Styling | CSS Modules + Global CSS variables |
| Notifications | React Toastify |
| Routing | React Router DOM |

---

## Features

- **Products** — Full CRUD with paginated listing
- **Raw Materials** — Full CRUD with stock quantity management and visual stock indicators
- **Associations** — Link multiple raw materials to a product with required quantities; edit each association row individually
- **Production Suggestion** — Lists products that can be manufactured with current stock, ordered by highest value, with total production value summary

---
## Getting Started

### Prerequisites

- Node.js 18+
- Backend API running on `http://localhost:8080`

### Install & Run

```bash
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.
