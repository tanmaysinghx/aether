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
                    DEFAULT: 'var(--primary-default)',
                    hover: 'var(--primary-hover)',
                    foreground: colors.white,
                    dark: 'var(--primary-dark)',
                    'dark-hover': 'var(--primary-dark-hover)',
                },
                surface: {
                    DEFAULT: 'var(--colors-surface, #ffffff)',
                    dark: 'var(--colors-surface-dark, #030712)', // gray-950
                    hover: 'var(--colors-surface-hover, #f9fafb)', // gray-50
                    'dark-hover': 'var(--colors-surface-dark-hover, #1f2937)', // gray-800
                },
                // Semantic grays
                border: {
                    DEFAULT: 'var(--colors-border, #e5e7eb)', // gray-200
                    dark: 'var(--colors-border-dark, #1f2937)', // gray-800
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
