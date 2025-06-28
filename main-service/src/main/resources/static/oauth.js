function base64urlEncode(buffer) {
    return btoa(String.fromCharCode(...new Uint8Array(buffer)))
        .replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

async function generateCodeChallenge(codeVerifier) {
    const encoder = new TextEncoder();
    const data = encoder.encode(codeVerifier);
    const digest = await crypto.subtle.digest("SHA-256", data);
    return base64urlEncode(digest);
}

async function startOAuthLogin() {
    const clientId = "shareit-frontend";
    const redirectUri = "http://localhost:9090/index.html";
    const state = crypto.randomUUID();
    const codeVerifier = crypto.randomUUID().replace(/-/g, "");
    const codeChallenge = await generateCodeChallenge(codeVerifier);

    sessionStorage.setItem("code_verifier", codeVerifier);
    sessionStorage.setItem("oauth_state", state);

    const authUrl = new URL("http://localhost:9093/oauth2/authorize");
    authUrl.searchParams.set("response_type", "code");
    authUrl.searchParams.set("client_id", clientId);
    authUrl.searchParams.set("redirect_uri", redirectUri);
    authUrl.searchParams.set("scope", "openid");
    authUrl.searchParams.set("state", state);
    authUrl.searchParams.set("code_challenge", codeChallenge);
    authUrl.searchParams.set("code_challenge_method", "S256");

    window.location.href = authUrl.toString();
}
