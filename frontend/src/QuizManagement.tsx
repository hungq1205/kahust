import React, { useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import { FaArrowLeft, FaEdit, FaPlus, FaTrash } from "react-icons/fa";
import "bootstrap/dist/css/bootstrap.min.css";
import { fetchQuizSet, fetchQuizSets, fetchRequest, getRequest, useUser } from "./UserUtil";
import { backend } from "./config";

export interface Quiz {
  id: number;
  questionContent: string;
  correctOptionIdx: number;
  optionContentA: string;
  optionContentB: string;
  optionContentC: string;
  optionContentD: string;
}

export interface QuizSet {
  id: number;
  ownerId: number;
  name: string;
  quizzes: Quiz[];
}

const QuizItem: React.FC<{ quiz: Quiz; onEdit: () => void; onDelete: () => void }> = ({ quiz, onEdit, onDelete }) => (
  <div className="border p-3 mb-3 rounded bg-light">
    <div className="d-flex align-items-center justify-content-between">
      <span className="fw-bold">{quiz.questionContent}</span>
      <div className="ms-3 d-flex gap-2">
        <FaEdit role="button" size={20} onClick={onEdit} />
        <FaTrash className="text-danger" role="button" size={20} onClick={onDelete} />
      </div>
    </div>
    <ul className="mt-2 mb-0">
      {[quiz.optionContentA, quiz.optionContentB, quiz.optionContentC, quiz.optionContentD].map((option, index) => (
        option && (
          <li key={index} className={quiz.correctOptionIdx === index ? "fw-bold text-success" : ""}>
            {option} {quiz.correctOptionIdx === index}
          </li>
        )
      ))}
    </ul>
  </div>
);

const postSet = async (name: string, onFetchSucceed: () => void) => {
  try {
    const res = await fetchRequest("POST", "/quizzes/sets", { name: name });
    if (res.ok || res.status === 201)
      onFetchSucceed();
    else
      console.error("Failed to create quiz set", res.statusText);
  } catch (error) {
    console.error("Failed to create quiz set", error);
  }
}

const deleteSet = async (id: number, onFetchSucceed: () => void) => {
  try {
    const res = await fetchRequest("DELETE", `/quizzes/sets/${id}`, {});
    if (res.ok || res.status === 201)
      onFetchSucceed();
    else
      console.error("Failed to delete quiz set", res.statusText);
  } catch (error) {
    console.error("Failed to delete quiz set", error);
  }
}

const QuizSetManagement: React.FC = () => {
  const [quizSets, setQuizSets] = useState<QuizSet[]>([]);
  const [newSetName, setNewSetName] = useState("");
  const [selectedSet, setSelectedSet] = useState<QuizSet | null>(null);
  const { user, fetchDone } = useUser();

  useEffect(() => {
    if (fetchDone) fetchQuizSets(setQuizSets);
  }, [user, fetchDone])

  const addSet = () => {
    if (!newSetName.trim()) return;
    postSet(newSetName, () => fetchQuizSets(setQuizSets));
    setNewSetName("");
  };

  const setManagementLayout = () => {
      return <div className="mt-5 pt-4">
        <h2 className="text-light mb-4">Quiz Sets</h2>
        <div className="d-flex mb-4">
          <Form.Control
            type="text"
            placeholder="New Set Name"
            value={newSetName}
            onChange={(e) => setNewSetName(e.target.value)}
            style={{ height: "60px" }}
          />
          <div className="ms-2 position-relative d-flex align-items-center px-2" role="button" onClick={addSet}>
            <FaPlus size={35} className="text-light" />
          </div>
        </div>
        {quizSets.map((set) => (
          <div key={set.id} className="border mb-3 rounded bg-light">
            <div className="d-flex align-items-center justify-content-between" >
              <div className="fw-bold flex-grow-1 p-3 px-4" role="button" onClick={() => setSelectedSet(set)}>
                {set.name}
              </div>
              <FaTrash className="text-danger sizing-content p-3 px-4" role="button" size={20} onClick={() => deleteSet(set.id, () => fetchQuizSets(setQuizSets))} />
            </div>
          </div>
        ))}
      </div>
  }

  return (
    <div className="container py-5">
      {selectedSet ? <QuizManagement quizSetId={selectedSet.id} onBack={() => setSelectedSet(null)} /> : setManagementLayout()}
    </div>
  );
};

const postQuiz = async (setId: number, quiz: Quiz, onFetchSucceed: () => void) => {
  try {
    const res = await fetchRequest("POST", `/quizzes/sets/${setId}/collection`, quiz);
    if (res.ok || res.status === 201)
      onFetchSucceed();
    else
      console.error("Failed to create quiz", res.statusText);

  } catch (error) {
    console.error("Failed to create quiz", error);
  }
}

const putQuiz = async (quiz: Quiz, onFetchSucceed: () => void) => {
  try {
    const res = await fetchRequest("PUT", `/quizzes/${quiz.id}`, quiz);
    if (res.ok || res.status === 201)
      onFetchSucceed();
    else
      console.error("Failed to update quiz", res.statusText);

  } catch (error) {
    console.error("Failed to update quiz", error);
  }
}

const deleteQuiz = async (setId: number, id: number, onFetchSucceed: () => void) => {
  try {
    const res = await fetchRequest("DELETE", `/quizzes/sets/${setId}/collection/${id}`, {});
    if (res.ok || res.status === 201)
      onFetchSucceed();
    else
      console.error("Failed to delete quiz", res.statusText);
  } catch (error) {
    console.error("Failed to delete quiz", error);
  }
}

const QuizManagement: React.FC<{ quizSetId: number; onBack: () => void }> = ({ quizSetId, onBack }) => {
  const [quizSet, setQuizSet] = useState<QuizSet | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [editingQuiz, setEditingQuiz] = useState<Quiz | null>(null);
  const [newQuiz, setNewQuiz] = useState<Quiz>({
    id: -1,
    questionContent: "Question",
    correctOptionIdx: 0,
    optionContentA: "Option 1",
    optionContentB: "Option 2",
    optionContentC: "Option 3",
    optionContentD: "Option 4",
  });

  useEffect(() => {
    fetchQuizSet(quizSetId, setQuizSet);
  }, [quizSetId]);

  if (!quizSet) return null;

  const handleAddOrEditQuizDone = () => {
    if (!newQuiz.questionContent.trim()) return;

    if (editingQuiz)
      putQuiz(newQuiz, () => fetchQuizSet(quizSet.id, setQuizSet));
    else
      postQuiz(quizSet.id, newQuiz, () => fetchQuizSet(quizSet.id, setQuizSet));

    setShowModal(false);
    setEditingQuiz(null);
  };
  
  const handleEditQuiz = (quiz: Quiz) => {
    setEditingQuiz(quiz);
    setNewQuiz(quiz);
    setShowModal(true);
  };

  const handleNewQuiz = () => {
    setEditingQuiz(null);
    setNewQuiz({
      id: -1,
      questionContent: "Question",
      correctOptionIdx: 0,
      optionContentA: "Option 1",
      optionContentB: "Option 2",
      optionContentC: "Option 3",
      optionContentD: "Option 4",
    });
    setShowModal(true);
  };

  const handleDeleteQuiz = (id: number) => {
    deleteQuiz(quizSet.id, id, () => fetchQuizSet(quizSet.id, setQuizSet));
  };
  
  return (
    <div className="container py-5">
      <h2 className="text-light">Quiz Set: {quizSet.name}</h2>
      <div className="my-4 d-flex justify-content-between">
        <FaArrowLeft className="p-3 mx-2 text-light" size={60} onClick={onBack} role="button"></FaArrowLeft>
        <Button variant="light" onClick={handleNewQuiz} className="p-3">Add New Quiz</Button>
      </div>
      {quizSet.quizzes.sort((a: any, b: any) => a.id - b.id).map((quiz) => (
        <QuizItem key={quiz.id} quiz={quiz} onEdit={() => handleEditQuiz(quiz)} onDelete={() => handleDeleteQuiz(quiz.id)} />
      ))}
      <QuizModal show={showModal} onClose={() => setShowModal(false)} quiz={newQuiz} setQuiz={setNewQuiz} onSave={handleAddOrEditQuizDone} isEditing={!!editingQuiz} />
    </div>
  );
};

const QuizModal: React.FC<{ show: boolean; onClose: () => void; quiz: Quiz; setQuiz: (quiz: Quiz) => void; onSave: () => void; isEditing: boolean }> = ({ show, onClose, quiz, setQuiz, onSave, isEditing }) => (
  <Modal show={show} onHide={onClose}>
    <Modal.Header closeButton>
      <Modal.Title>{isEditing ? "Edit Quiz" : "Create New Quiz"}</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      <Form>
        <Form.Group className="mb-3">
          <Form.Label>Question</Form.Label>
          <Form.Control
            type="text"
            value={quiz.questionContent}
            onChange={(e) => setQuiz({ ...quiz, questionContent: e.target.value })}
          />
        </Form.Group>
        {"A B C D".split(" ").map((label, index) => (
          <Form.Group key={index} className="mb-2 d-flex align-items-center">
            <Form.Check
              type="radio"
              name="correctOption"
              checked={quiz.correctOptionIdx === index}
              onChange={() => setQuiz({ ...quiz, correctOptionIdx: index })}
              className="mx-3"
            />
            <Form.Control
              type="text"
              value={(quiz as any)[`optionContent${label}`]}
              onChange={(e) => setQuiz({ ...quiz, [`optionContent${label}`]: e.target.value })}
            />
          </Form.Group>
        ))}
      </Form>
    </Modal.Body>
    <Modal.Footer>
      <Button variant="secondary" onClick={onClose}>Cancel</Button>
      <Button variant="danger" onClick={onSave} disabled={!quiz.questionContent.trim()}>{isEditing ? "Save Changes" : "Add Quiz"}</Button>
    </Modal.Footer>
  </Modal>
);

const App: React.FC = () => {
  return <QuizSetManagement />;
};

export default App;
