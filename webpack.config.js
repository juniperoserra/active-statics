const path = require('path');
const webpack = require('webpack');

// env will be "dev" or "prod"
module.exports = function (env) {
    return {
        context: path.resolve(__dirname, './src'),
        entry: {
            app: './app.js'
        },
        output: {
            filename: '[name].bundle.js',
            path: path.resolve(__dirname, './dist/assets'),
            publicPath: '/dist/assets',
        },
        devServer: {
            static: path.resolve(__dirname, './src'), // Updated for Webpack 5
        },
        devtool: env === 'dev' ? 'inline-source-map' : 'source-map', // Enable source maps
        module: {
            rules: [
                {
                    test: /\.js$/,
                    use: [
                        'babel-loader',
                    ],
                    exclude: /node_modules/
                },
                {
                    test: /\.css$/,
                    use: [
                        'style-loader',
                        {
                            loader: 'css-loader',
                            options: {
                                modules: {
                                    auto: true, // Automatically enable CSS modules for files matching /\.module\.css$/
                                    localIdentName: '[name]__[local]__[hash:base64:5]', // Customize class names
                                },
                            },
                        },
                    ],
                },
            ],
        }
    }
};