# tube UI

**tube UI** is the modern frontend client for the Aether Video Engine. Built with **Angular 21**, it features a sleek interface styled with **Tailwind CSS** and **Preline UI components**.

## 🛠️ Technology Stack

*   **Framework**: [Angular 21.0.4](https://angular.dev/)
*   **Styling**: [Tailwind CSS 3.4](https://tailwindcss.com/)
*   **Components**: [Preline UI](https://preline.co/)
*   **Video Player**: HLS.js (Integrated)

## 🚀 Getting Started

### Prerequisites
*   Node.js v20+
*   NPM

### Installation

1.  Navigate to the directory:
    ```bash
    cd aetherTube-ui-1250
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```

### Running Locally

Start the development server:

```bash
npm start
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## 📦 Build

To build the project for production:

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## 🎨 Styling & Configuration

*   **Tailwind Config**: `tailwind.config.js`
*   **Preline Integration**: Scripts are loaded via `angular.json` scripts array.
*   **Global Styles**: `src/styles.scss`

## 🤝 Contributing

1.  Run `ng generate component component-name` to generate a new component.
2.  Ensure linting passes before committing.
