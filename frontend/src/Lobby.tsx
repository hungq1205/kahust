import React, { useEffect, useState } from "react";
import { Button, Form } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { backend } from "./config";
import { fetchQuizSets, fetchRoomOfUser, useUser } from "./UserUtil";
import { QuizSet } from "./QuizManagement";

const postJoinRoom = async (roomId: number, onFetchSucceed: () => void) => {  
    try {
        const res = await fetch(`${backend}/rooms/${roomId}/participants`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
                "Content-Type": "application/json"
            }
        });
        if (res.ok || res.status === 201)
            onFetchSucceed();
        else
            console.log(await res.text())
    } catch (error) {
        console.error(error);
    }
}

const postCreateRoom = async (setId: number, onFetchSucceed: () => void) => {
    try {
        const res = await fetch(`${backend}/rooms`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ 
                quizSetId: setId 
            })
        });
        if (res.ok || res.status === 201)
            onFetchSucceed();
        else
            console.log(await res.text())
    } catch (error) {
        console.error(error);
    }
}

const Lobby: React.FC = () => {
  const [roomId, setRoomId] = useState("");
  const [selectedSetId, setSelectedSetId] = useState(-1);
  const [selectedSet, setSelectedSet] = useState("");
  const [quizSets, setQuizSets] = useState<QuizSet[]>([]);
  const { user, setRoom, fetchDone } = useUser();

  useEffect(() => {
    if (fetchDone)
      fetchQuizSets(setQuizSets);
  }, [user, fetchDone]);

  return (
    <div className="container py-5 text-center d-flex flex-column align-items-center justify-content-center vh-100">
      <Button variant="primary" className="mb-3 p-4 px-5 position-relative" onClick={() => window.location.href = "/quiz-management"}>
        <div className="overlay-dark" />
        Manage Quizzes
      </Button>
      <div className="d-flex justify-content-center gap-4">
        <div className="border p-4 rounded bg-light" style={{ width: "300px" }}>
          <h4>Create Room</h4>
          <Form.Select className="mb-2" value={selectedSet} onChange={(e) => {
                const selectedQuizSet = quizSets.find(set => set.name === e.target.value);
                if (!selectedQuizSet)
                    return;
                setSelectedSet(selectedQuizSet.name);
                setSelectedSetId(selectedQuizSet.id);
            }}>
            <option value="">Select a Quiz Set</option>
            {quizSets.map((set) => (
              <option key={set.id} data-id={set.id} value={set.name}>{set.name}</option>
            ))}
          </Form.Select>
          <Button variant="primary" className="p-3 px-4 mt-2" disabled={!selectedSet} onClick={() => postCreateRoom(selectedSetId, () => fetchRoomOfUser(user?.id, setRoom))}>
            Create Room
          </Button>
        </div>
        <div className="border p-4 rounded bg-light" style={{ width: "300px" }}>
          <h4>Join Room</h4>
          <Form.Control
            type="text"
            placeholder="Enter Room ID"
            value={roomId}
            onChange={(e) => setRoomId(e.target.value)}
            className="mb-2"
          />
          <Button variant="primary" className="p-3 px-4 mt-2" disabled={!Number(roomId) || Number(roomId) < 0} onClick={() => postJoinRoom(Number(roomId), () => fetchRoomOfUser(user?.id, setRoom))}>
            Join Room
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Lobby;
