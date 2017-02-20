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
            publicPath: '/assets',                          // New
        },
        devServer: {
            contentBase: path.resolve(__dirname, './src'),  // New
        },
        devtool: env === 'dev' ? 'cheap-module-eval-source-map' : 'cheap-module-source-map',
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
                        'css-loader?modules',
                        'postcss-loader',
                    ],
                },
            ],
        }
    }
};