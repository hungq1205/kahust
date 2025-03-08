import { createContext, MutableRefObject, useContext, useEffect, useRef, useState } from "react";
import { QuizSet } from "./QuizManagement";
import { backend } from "./config";

export interface Room {
  id: number;
  ownerId: number;
  state: "IDLE" | "PLAYING" | "REVIEW";
  quizTimeout: number;
  quizSetId: number | null;
  currentQuizIdx: number;
  participants: Participant[];
}

export interface Participant {
  userId: number
  username: string
  score: number
  answerIdx: number
}

export interface User {
  id: number
  username: string
}

interface UserContextType {
  user: User | null
  setUser: (user: User | null) => void
  room: Room | null
  setRoom: (room: Room | null) => void
  fetchDone: boolean
  setFetchDone: (fetchDone: boolean) => void
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within a UserProvider");
  }
  return context;
};

export const UserProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [room, setRoom] = useState<Room | null>(null);
  const [fetchDone, setFetchDone] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      const userTemp = await fetchUser(setUser);
      if (userTemp)
        await fetchRoomOfUser(userTemp.id, setRoom);
      setFetchDone(true);
    };
    fetchData();
  }, []);

  return (
    <UserContext.Provider value={{ user, setUser, room, setRoom, fetchDone, setFetchDone }}>
      {children}
    </UserContext.Provider>
  );
};

export const fetchRoomOfUser = async (userId: number | undefined, setRoom: (room: Room | null) => void) => {
  if (!userId || !localStorage.getItem("token"))
    return;
  try {
    const res = await fetch(`${backend}/rooms/ofUser/${userId}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json"
      }
    });
    if (!res.ok)
      setRoom(null)
    else
      setRoom(await res.json());
  } catch (error) {
    setRoom(null);
    console.log(error);
  }
}

export const fetchUser = async (setUser: (user: User | null) => void) => {
  try {
    const res = await fetch(`${backend}/users/me`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json"
      }
    });
    if (!res.ok) {
      setUser(null);
      return
    }
    const data = await res.json();
    setUser(data);
    return data;
  } catch (error) {
    setUser(null);
    console.error(error);
  } 
}

export const fetchQuizSets = async (setQuizSets: (user: QuizSet[]) => void) => {
  try {
    const res = await fetch(`${backend}/quizzes/sets`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json"
      }
    });
    if (!res.ok) {
      setQuizSets([]);
      return
    }
    const data = await res.json();
    setQuizSets(data);
  } catch (error) {
    setQuizSets([]);
    console.error(error);
  }
  return null;
}

export const fetchQuizSet = async (setId: number, setQuizSet: (quizSet: QuizSet | null) => void) => {
  try {
    const res = await getRequest(`/quizzes/sets/${setId}`)
    if (!res.ok) {
      setQuizSet(null);
      console.error("Failed to fetch quizzes", res.statusText);
    } else
      setQuizSet((await res.json()));
  } catch (error) {
    setQuizSet(null);
    console.error("Failed to fetch quizzes", error);
  }
}

export const getRequest = async (pathname: string) => {
  return await fetch(`${backend}${pathname}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("token")}`,
      "Content-Type": "application/json"
    },
  });
}

export const fetchRequest = async (method: string, pathname: string, data: any) => {
  return await fetch(`${backend}${pathname}`, {
    method: method,
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("token")}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  });
}