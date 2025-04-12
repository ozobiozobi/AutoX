// @ts-check
import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';

export default tseslint.config(
    {
        files: ["src/**/*.ts"],
        extends: [
            eslint.configs.recommended,
            tseslint.configs.recommended,
        ],
        rules: {
            "no-var": "off",
            'no-unused-vars': 'off',
            'prefer-rest-params': 'off',
            "@typescript-eslint/no-explicit-any": 'off',
            "@typescript-eslint/no-unused-vars": 'warn'
        }
    }
)