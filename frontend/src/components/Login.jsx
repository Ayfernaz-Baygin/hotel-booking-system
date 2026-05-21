import { useState } from "react";
import { supabase } from "../supabase";

function Login({ onLogin }) {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("CUSTOMER");
  const [loading, setLoading] = useState(false);

  async function signUp() {

    setLoading(true);

    const { data, error } = await supabase.auth.signUp({
      email,
      password,
      options: {
        data: {
          role: role
        }
      }
    });

    setLoading(false);

    if (error) {
      alert(error.message);
      return;
    }

    alert("User created successfully!");

    console.log("REGISTER RESPONSE:", data);
  }

  async function signIn() {

    setLoading(true);

    const { data, error } = await supabase.auth.signInWithPassword({
      email,
      password
    });

    setLoading(false);

    if (error) {
      alert(error.message);
      return;
    }

    console.log("LOGIN RESPONSE:", data);

    const session = data.session;

    if (!session) {
      alert("No session found.");
      return;
    }

    const userRole = session.user.user_metadata?.role || "CUSTOMER";

    console.log("ROLE:", userRole);
    console.log("TOKEN:", session.access_token);

    localStorage.setItem("access_token", session.access_token);
    localStorage.setItem("user_role", userRole);

    alert("Login successful!");

    if (onLogin) {
      onLogin({
        token: session.access_token,
        role: userRole,
        user: session.user
      });
    }
  }

  return (
    <div
      style={{
        background: "white",
        padding: "30px",
        borderRadius: "20px",
        width: "380px",
        margin: "40px auto",
        boxShadow: "0 0 20px rgba(0,0,0,0.1)"
      }}
    >
      <h2
        style={{
          color: "#1e3a8a",
          textAlign: "center",
          marginBottom: "20px"
        }}
      >
        Hotel Booking Authentication
      </h2>

      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        style={{
          width: "100%",
          padding: "12px",
          marginTop: "10px",
          borderRadius: "10px",
          border: "1px solid #ccc"
        }}
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{
          width: "100%",
          padding: "12px",
          marginTop: "15px",
          borderRadius: "10px",
          border: "1px solid #ccc"
        }}
      />

      <select
        value={role}
        onChange={(e) => setRole(e.target.value)}
        style={{
          width: "100%",
          padding: "12px",
          marginTop: "15px",
          borderRadius: "10px",
          border: "1px solid #ccc",
          background: "white"
        }}
      >
        <option value="CUSTOMER">Customer</option>
        <option value="ADMIN">Admin</option>
      </select>

      <button
        onClick={signIn}
        disabled={loading}
        style={{
          width: "100%",
          padding: "12px",
          marginTop: "20px",
          borderRadius: "10px",
          border: "none",
          background: "#2563eb",
          color: "white",
          fontWeight: "bold",
          cursor: "pointer",
          fontSize: "16px",
          opacity: loading ? 0.7 : 1
        }}
      >
        {loading ? "Loading..." : "Login"}
      </button>

      <button
        onClick={signUp}
        disabled={loading}
        style={{
          width: "100%",
          padding: "12px",
          marginTop: "10px",
          borderRadius: "10px",
          border: "none",
          background: "#16a34a",
          color: "white",
          fontWeight: "bold",
          cursor: "pointer",
          fontSize: "16px",
          opacity: loading ? 0.7 : 1
        }}
      >
        {loading ? "Loading..." : "Register"}
      </button>
    </div>
  );
}

export default Login;