import React, { useState } from "react";
import { Button, Form, Container, Card } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { useUser, fetchUser } from "./UserUtil";
import { backend } from "./config";

const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSignup, setIsSignup] = useState(false);
  const { setUser } = useUser();

  const handleAction = async () => {
    if (isSignup) {
      if (password !== confirmPassword) {
        alert("Passwords do not match");
        return;
      }
      try {
        const response = await fetch(`${backend}/auth/signup`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password })
        });
        if (!response.ok) alert("Signup failed");
        alert("Signed up successfully");
        setIsSignup(false);
      } catch (error) {
        console.error(error);
      }
    } else {
      try {
        const response = await fetch(`${backend}/auth/login`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password })
        });
        if (!response.ok) alert("Login failed");
        localStorage.setItem("token", (await response.json()).token);
        fetchUser(setUser)
      } catch (error) {
        console.error(error);
      }
    }
  };

  return (
    <Container
      className="d-flex justify-content-center align-items-center vh-100 bg-danger"
    >
      <Card className="p-4" style={{ width: "350px" }}>
        <h2 className="text-center text-danger">{isSignup ? "Sign Up" : "Login"}</h2>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              onKeyDown={e => e.key === "Enter" && handleAction()}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              onKeyDown={e => e.key === "Enter" && handleAction()}
            />
          </Form.Group>
          {isSignup && (
            <Form.Group className="mb-3">
              <Form.Label>Confirm Password</Form.Label>
              <Form.Control
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Retype password"
              />
            </Form.Group>
          )}
          <Button variant="danger" className="w-100" onClick={handleAction}>
            {isSignup ? "Sign Up" : "Login"}
          </Button>
        </Form>
        <div className="text-center mt-3">
          <Button variant="link" onClick={() => setIsSignup(!isSignup)}>
            {isSignup ? "Already have an account? Login" : "Don't have an account? Sign Up"}
          </Button>
        </div>
      </Card>
    </Container>
  );
};

export default Login;
