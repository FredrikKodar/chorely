/** @type {import('tailwindcss').Config} */
const forms = require('@tailwindcss/forms');
const typography = require('@tailwindcss/typography');

module.exports = {
  content: [
    './src/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  darkMode: 'class', // This enables dark mode via .dark class
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [
    forms,
    typography,
  ],
}
