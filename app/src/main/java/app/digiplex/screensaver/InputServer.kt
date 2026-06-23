package app.digiplex.screensaver

import fi.iki.elonen.NanoHTTPD

class InputServer(
    port: Int,
    private val onUrlSubmitted: (String) -> Unit
) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        if (session.method == Method.POST) {
            val body = mutableMapOf<String, String>()
            session.parseBody(body)
            val params = session.parameters
            val rawUrl = params["url"]?.firstOrNull()?.trim().orEmpty()
            if (rawUrl.isNotEmpty()) {
                val url = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
                    "https://$rawUrl"
                } else rawUrl
                if (!isValidUrl(url)) {
                    return newFixedLengthResponse(Response.Status.OK, "text/html", errorHtml(rawUrl))
                }
                onUrlSubmitted(url)
                return newFixedLengthResponse(Response.Status.OK, "text/html", successHtml(url))
            }
        }
        return newFixedLengthResponse(Response.Status.OK, "text/html", FORM_HTML)
    }

    private fun successHtml(url: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body { font-family: -apple-system, system-ui, sans-serif; max-width: 480px;
                       margin: 40px auto; padding: 0 20px; background: #111; color: #eee; }
                .card { background: #1a3a1a; border-radius: 12px; padding: 24px; text-align: center; }
                .check { font-size: 48px; margin-bottom: 12px; }
                .url { color: #8f8; word-break: break-all; font-size: 14px; margin-top: 12px; }
                a { color: #8af; }
            </style>
        </head>
        <body>
            <div class="card">
                <div class="check">&#10003;</div>
                <h2>URL saved!</h2>
                <p class="url">${url.replace("<", "&lt;").replace(">", "&gt;")}</p>
                <p>Your TV screensaver will now show this website.</p>
                <p><a href="/">Change URL</a></p>
            </div>
        </body>
        </html>
    """.trimIndent()

    private fun isValidUrl(url: String): Boolean {
        return try {
            val parsed = java.net.URL(url)
            parsed.protocol in listOf("http", "https") && parsed.host.contains(".")
        } catch (_: Exception) {
            false
        }
    }

    private fun errorHtml(input: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body { font-family: -apple-system, system-ui, sans-serif; max-width: 480px;
                       margin: 40px auto; padding: 0 20px; background: #111; color: #eee; }
                .card { background: #3a1a1a; border-radius: 12px; padding: 24px; text-align: center; }
                .icon { font-size: 48px; margin-bottom: 12px; }
                .input { color: #f88; word-break: break-all; font-size: 14px; margin-top: 12px; }
                a { color: #8af; }
            </style>
        </head>
        <body>
            <div class="card">
                <div class="icon">&#10007;</div>
                <h2>Invalid URL</h2>
                <p class="input">${input.replace("<", "&lt;").replace(">", "&gt;")}</p>
                <p>Please enter a valid website address<br>(e.g. www.example.com)</p>
                <p><a href="/">Try again</a></p>
            </div>
        </body>
        </html>
    """.trimIndent()

    companion object {
        const val PORT = 8888

        private val FORM_HTML = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                    body { font-family: -apple-system, system-ui, sans-serif; max-width: 480px;
                           margin: 40px auto; padding: 0 20px; background: #111; color: #eee; }
                    h2 { text-align: center; }
                    input[type=text] { width: 100%; padding: 14px; font-size: 16px; border: 2px solid #444;
                                      border-radius: 8px; background: #222; color: #eee; box-sizing: border-box; }
                    input[type=text]:focus { border-color: #58f; outline: none; }
                    button { width: 100%; padding: 14px; font-size: 16px; margin-top: 12px;
                             background: #38f; color: white; border: none; border-radius: 8px; cursor: pointer; }
                    button:hover { background: #26d; }
                </style>
            </head>
            <body>
                <h2>TV Web Screensaver</h2>
                <form method="POST">
                    <input type="text" name="url" placeholder="www.example.com"
                           required autofocus autocapitalize="off" autocorrect="off" />
                    <button type="submit">Set URL</button>
                </form>
            </body>
            </html>
        """.trimIndent()
    }
}
