import Lobby from './Lobby.tsx';
import Header from './Header.tsx';
import './App.css';
import ClientRoom from './ClientRoom.tsx';
import QuizManagement from './QuizManagement.tsx';
import Login from './Login.tsx';
import { UserProvider, useUser } from './UserUtil.tsx';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useEffect } from 'react';

function App() {
	return (
		<>
		<UserProvider>
			<AuthHandler />
			<Routes>
				<Route element={<Header />}>
					<Route path="/login" element={<Login />} />
					<Route>
						<Route path="/lobby" element={<Lobby />} />
						<Route path="/room" element={<ClientRoom />} />
						<Route path="/quiz-management" element={<QuizManagement />} />
					</Route>
					<Route path="*" element={<Navigate to="/login" />} />
				</Route>
			</Routes>
		</UserProvider>
		</>
	);
}

const AuthHandler: React.FC = () => {
	const { user, room, fetchDone } = useUser();

	useEffect(() => {
		if (!fetchDone)
			return
		if (!user) {
			if (window.location.pathname !== "/login")
				window.location.href = "/login";
		} else if (window.location.pathname === "/login") {
			window.location.href = "/lobby"
		} else if (room) {
			if (window.location.pathname !== "/room")
				window.location.href = "/room";
		} else if (window.location.pathname === "/room") {
			window.location.href = "/lobby";
		}
	}, [fetchDone, user, room]);

	return null;
};

export default App;