import React, { useState, useEffect, useRef } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { fetchQuizSet, fetchRequest, fetchRoomOfUser, Participant, Room, useUser } from "./UserUtil";
import { QuizSet } from "./QuizManagement";
import { FaCaretRight } from "react-icons/fa6";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { ws_url } from "./config";

interface ScoreboardEntryProps {
  name: string;
  points: number;
}

const STATE_NAMES = ["IDLE", "PLAYING", "REVIEW"] as const;
type RoomState = typeof STATE_NAMES[number];

const SOCKET_MSG_NAMES = ["ROOM_UPDATE", "ROOM_NEXT", "ROOM_REVEAL"] as const;
type SocketMsg = typeof SOCKET_MSG_NAMES[number];

const ScoreboardEntry: React.FC<ScoreboardEntryProps> = ({ name, points }) => (
  <li className="d-flex justify-content-between p-3 bg-light rounded mb-2 fs-5">
    <span>{name}</span>
    <span className="text-danger fw-bold">{points} pts</span>
  </li>
);

interface LobbyProps {
  room: Room;
  userId: number;
  onStart: () => void;
  onExit: () => void;
}

const Lobby: React.FC<LobbyProps> = ({ room, userId, onStart, onExit }) => {
  const isOwner = userId === room.ownerId;
  const hanleExit = async () => {
    const res = isOwner ?
      await fetchRequest("DELETE", `/rooms/${room.id}`, {}) :
      await fetchRequest("DELETE", `/rooms/${room.id}/participants`, {userId:userId})
      onExit();
    if (res.ok || res.status === 204)
      window.location.href = "/lobby";
    else
      console.error("Failed to exit room", res.statusText);
  }

  return (
  <div className="vh-100 bg-danger text-white d-flex flex-column align-items-center justify-content-center">
    <h2 className="fw-bold text-light">#{room.id}</h2>
    <h1 className="display-4 fw-bold">Lobby</h1>
    <ul className="mt-3 list-unstyled">
      {room.participants.map(participant => (
        participant.userId !== room.ownerId && <li key={participant.userId} className="fs-5">{participant.username}</li>
      ))}
    </ul>
    <div className="position-absolute bottom-0 end-0 m-4 mx-5 d-flex gap-3">
      <button onClick={hanleExit} className="btn btn-light text-danger p-3 px-4">
        <h4 className="font-weight-bold">Exit</h4>
      </button>
      {isOwner && <button onClick={onStart} className="btn btn-light text-danger p-3 px-4">
        <h4 className="font-weight-bold">Start Game</h4>
      </button> }
    </div>
  </div>
)};

interface GameProps {
  quizSet: QuizSet;
  room: Room;
  participant: Participant;
  answerIdx: number;
  reveal: boolean;
  onSelect: (quizIdx: number, index: number) => void;
  onNext: () => void;
}

const Game: React.FC<GameProps> = ({ quizSet, room, participant, reveal, answerIdx, onSelect, onNext }) => {
  const isOwner = participant.userId === room.ownerId;
  const quiz = quizSet.quizzes[room.currentQuizIdx];
  const getBtnClass = (idx: number) => {
    if (reveal && quiz.correctOptionIdx === idx)
      return "btn-danger";
    if ((reveal || answerIdx !== -1) && answerIdx !== idx) 
      return "btn-secondary";
    return ["btn-primary", "btn-success", "btn-warning", "btn-info"][idx]
  }

  const options = [
    quiz.optionContentA,
    quiz.optionContentB,
    quiz.optionContentC,
    quiz.optionContentD,
  ].filter(option => !!option);
  
  const ownerControls = () => {
    const participantNum = room.participants.length - 1;
    const answerNum = room.participants.filter(p => p.answerIdx !== -1).length;
    return (
      <div className="position-absolute bottom-0 end-0  h-100 d-flex flex-row align-items-center gap-4">
        <h3 className="text-light fw-bold m-0">{answerNum} / {participantNum}</h3>
        <button onClick={onNext} className="btn btn-light text-danger">
          <FaCaretRight size={55}></FaCaretRight>
        </button>
      </div>
    )
  }

  return (
  <div className="vh-100 pt-5 d-flex flex-column position-relative" style={{ background: "rgb(25, 29, 33)" }}>
    <div className="flex-grow-1 d-flex align-items-center justify-content-center bg-danger text-white display-5 fw-bold p-4">
      {quizSet.quizzes[room.currentQuizIdx].questionContent}
    </div>
    <div className="py-3 position-relative mx-4">
      <div className="container m-auto">
        <div className="row g-2">
          {options.map((option, idx) => (
            <div key={idx} className="col-6 flex-grow-1">
              <button
                onClick={() => onSelect(quiz.id, idx)}
                className={`btn w-100 py-3 text-white fw-bold fs-4 ${getBtnClass(idx)}`}
                disabled={isOwner || answerIdx !== -1 || reveal}
              >
                {option}
              </button>
            </div>
          ))}
        </div>
      </div>
      { isOwner && ownerControls() }
    </div>
  </div>
)};

interface GameEndProps {
  room: Room;
  userId: number;
  onBack: () => void;
}

const GameEnd: React.FC<GameEndProps> = ({ room, userId, onBack }) => (
  <div className="vh-100 bg-danger text-white d-flex flex-column align-items-center justify-content-center">
    <h1 className="display-4 fw-bold mb-4">Final Scores</h1>
    <div className="bg-white rounded p-4 shadow text-black w-50">
      <ul className="list-unstyled">
        {room.participants.map(participant => (
          participant.userId !== room.ownerId && <ScoreboardEntry key={participant.userId} name={participant.username} points={participant.score} />
        ))}
      </ul>
    </div>
    { room.ownerId === userId && <button onClick={onBack} className="btn btn-light text-danger position-absolute bottom-0 end-0 shadow-lg m-4 p-5 pt-4 pb-4 btn-lg">
      <h4 className="font-weight-bold">Back To Lobby</h4>
    </button> }
  </div>
);

const ClientRoom: React.FC = () => {
  const [state, setState] = useState<RoomState>("IDLE");
  const [quizSet, setQuizSet] = useState<QuizSet | null>(null);
  const [answer, setAnswer] = useState(-1);
  const [reveal, setReveal] = useState(false);
  const [client, setClient] = useState<Client | null>(null);
  const participant = useRef<Participant | null>(null);
  const { user, room, setRoom, fetchDone } = useUser();
  const msgTimer = useRef(0);

  const sendMessage = (userId: number, content: SocketMsg) => {
    setTimeout(() => {
      sendMessageInternal(userId, content);
      msgTimer.current = msgTimer.current - 1;
    }, 50 * msgTimer.current);
    msgTimer.current = msgTimer.current + 1;
  }

  const sendMessageInternal = (userId: number, content: SocketMsg) => {
    if (client && client.connected) {
      const roomId = room!.id;
        client.publish({
            destination: `/app/${roomId}`,
            body: JSON.stringify({ userId, content, roomId }),
        });
    }
  };

  useEffect(() => {
    if (fetchDone && room && room.quizSetId)  {
      fetchQuizSet(room.quizSetId, setQuizSet);
      participant.current = room.participants.find(p => p.userId === user!.id) || null;
      setAnswer(participant.current?.answerIdx || -1);
      setState(room.state);
    }
  }, [room, user, fetchDone]);

  useEffect(() => {
    if (!fetchDone || !room)
      return;

    const stompClient = new Client({
        webSocketFactory: () => new SockJS(ws_url),
        onConnect: () => {
            stompClient.subscribe(`/room/${room.id}`, (msg) => {
              if (!user || !room) 
                return;

              const data = JSON.parse(msg.body);
              if (data.content == "ROOM_UPDATE") {
                if (data.userId !== user.id)
                  fetchRoomOfUser(user.id, setRoom);
                setReveal(false);
              }
              if (data.content == "ROOM_NEXT") {
                fetchRoomOfUser(user.id, setRoom);
                setReveal(false);
              }
              else if (data.content == "ROOM_REVEAL")
                setReveal(true);
            });

            sendMessage(user!.id, "ROOM_UPDATE");
        },
    });

    stompClient.activate();
    setClient(stompClient);

    return () => {
        stompClient.deactivate();
    };
  }, [fetchDone, room]);

  if (!fetchDone || !room || !participant.current)
    return null;

  const updateRoom = async (state: RoomState) => {
    const res = await fetchRequest("PUT", `/rooms/${room.id}`, { quizIdx: 0, state: state });
    if (res.ok) {
      const updatedRoom = await res.json();
      setRoom(updatedRoom);
      setState(updatedRoom.state);
      sendMessage(user!.id, "ROOM_UPDATE");
    } else {
      console.error("Failed to update room", res.statusText);
    }
  }

  const resetAnswers = async () => {
    await fetchRequest("POST", `/rooms/${room.id}/participants/resetAnswers`, {});
  }

  const resetScores = async () => {
    await fetchRequest("POST", `/rooms/${room.id}/participants/resetScores`, {});
  }

  const nextQuiz = async () => {
    if (!reveal) {
      setReveal(true);
      sendMessage(user!.id, "ROOM_REVEAL");
      return;
    }
    setReveal(false);
    if (room.currentQuizIdx === quizSet!.quizzes.length - 1) {
      updateRoom("REVIEW");
      setReveal(false);
      await resetAnswers();
      return;
    }
    const res = await fetchRequest("PUT", `/rooms/${room.id}`, { quizIdx: room.currentQuizIdx + 1 });
    sendMessage(user!.id, "ROOM_NEXT");
    if (res.ok) {
      const updatedRoom = await res.json();
      setRoom(updatedRoom);
      setState(updatedRoom.state);
      await resetAnswers();
    } else {
      console.error("Failed to update room", res.statusText);
    }
  }

  const chooseAnswer = async (quizId: number, answerIdx: number) => {
    const res = await fetchRequest("POST", `/rooms/${room.id}/participants/answer`, {quizId: quizId, answerIdx: answerIdx});
    if (res.ok) {
      setAnswer(answerIdx);
      const data = await res.json();
      if (data.isCorrect) {
        const res = await fetchRequest("POST", `/rooms/${room.id}/participants/score`, {score: participant.current!.score + 100});
        if (!res.ok)
          console.error("Failed to update score", res.statusText);
      }
    }
  }

  return (
    <div>
      {state === "IDLE" && <Lobby room={room} userId={user!.id} onStart={() => updateRoom("PLAYING")} onExit={() => sendMessage(user!.id, "ROOM_UPDATE")} />}

      {state === "PLAYING" && quizSet && <Game room={room} reveal={reveal} answerIdx={answer} participant={participant.current} quizSet={quizSet} onSelect={chooseAnswer} onNext={nextQuiz} />}

      {state === "REVIEW" && <GameEnd room={room} userId={user!.id} onBack={() => { updateRoom("IDLE"); resetScores() }} />}
    </div>
  );
};

export default ClientRoom;
