const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = (env, argv) => {
    const isDevelopment = argv.mode === 'development';

    return {
        context: path.resolve(__dirname, './src'),
        entry: {
            app: './app.js'
        },
        output: {
            filename: isDevelopment ? '[name].bundle.js' : 'assets/[name].bundle.js',
            path: path.resolve(__dirname, './dist'),
            publicPath: '/',
            clean: true
        },
        devServer: {
            static: {
                directory: path.resolve(__dirname, './dist'),
            },
            hot: true,
            port: 8080,
            host: '0.0.0.0',
            compress: true,
            allowedHosts: 'all'
        },
        devtool: isDevelopment ? 'inline-source-map' : 'source-map',
        module: {
            rules: [
                {
                    test: /\.js$/,
                    use: ['babel-loader'],
                    exclude: /node_modules/
                },
                {
                    test: /\.css$/,
                    use: [
                        'style-loader',
                        {
                            loader: 'css-loader',
                            options: {
                                modules: false
                            }
                        }
                    ],
                },
            ],
        },
        plugins: [
            new HtmlWebpackPlugin({
                template: path.resolve(__dirname, './src/index.html'),
                filename: 'index.html',
                inject: 'body',
                scriptLoading: 'defer'
            })
        ]
    }
};