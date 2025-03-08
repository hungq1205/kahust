import { Outlet } from "react-router-dom";
import { useUser } from "./UserUtil";
import { FaHome, FaSignOutAlt, FaUser } from "react-icons/fa";

const handleSignout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
}

const UserUI = ({ username }: { username: string }) => (
    <div className="d-flex align-items-center mx-3 text-secondary">
        <span className="fs-5 fw-semibold mx-3 mb-1">{username}</span>
        <FaSignOutAlt size={30} role="button" onClick={handleSignout} />
    </div>
)

const Header = () => {
    const { user } = useUser();
    return (
        <>
            <div className="d-flex justify-content-between align-items-center p-3 bg-light shadow fixed-top w-100">
                <div className="d-flex align-items-center">
                    <h2 className="text-danger mx-3 mb-1 fw-bold" role="button" onClick={() => window.location.href="/lobby"}>KaHust</h2>
                    <FaHome size={30} className="mb-0 mx-1 text-danger" role="button" onClick={() => window.location.href="/lobby"} />
                </div>
                { user && <UserUI username={user.username} /> }
            </div>
            <Outlet />
        </>
    )
}

export default Header;