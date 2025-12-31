const colors = require('tailwindcss/colors')

/** @type {import('tailwindcss').Config} */
module.exports = {
    darkMode: 'class',
    content: [
        "./src/**/*.{html,ts}",
        "./node_modules/preline/preline.js"
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    DEFAULT: colors.blue[600],
                    hover: colors.blue[700],
                    foreground: colors.white,
                    dark: colors.blue[500],
                    'dark-hover': colors.blue[600],
                },
                surface: {
                    DEFAULT: colors.white,
                    dark: colors.gray[950],
                    hover: colors.gray[50], // light hover
                    'dark-hover': colors.gray[800],
                },
                // Semantic grays
                border: {
                    DEFAULT: colors.gray[200],
                    dark: colors.gray[800],
                },
                text: {
                    main: colors.gray[900],        // dark text for light mode
                    muted: colors.gray[500],       // muted text
                    'dark-main': colors.gray[100], // light text for dark mode
                    'dark-muted': colors.gray[400],
                }
            }
        },
    },
    plugins: [
        // require('preline'),
    ],
}
