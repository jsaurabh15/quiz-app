package com.quiz.quizapp.service;

import com.quiz.quizapp.dao.QuestionDao;
import com.quiz.quizapp.dao.QuizDao;
import com.quiz.quizapp.model.Question;
import com.quiz.quizapp.model.QuestionWrapper;
import com.quiz.quizapp.model.Quiz;
import com.quiz.quizapp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
   @Autowired
   QuizDao quizDao;
   @Autowired
    QuestionDao questionDao;


   public ResponseEntity<String> createQuiz(String category, Integer numQ, String title) {
      try {
         List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);

         Quiz quiz = new Quiz();
         quiz.setTitle(title);
         quiz.setQuestions(questions);
         quizDao.save(quiz);

         return new ResponseEntity<>("success", HttpStatus.CREATED);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return new ResponseEntity<>(" ", HttpStatus.BAD_REQUEST);
   }

   public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
      try {
         Optional<Quiz> quiz = quizDao.findById(id);
         List<Question> questionsFromDB = quiz.get().getQuestions();
         List<QuestionWrapper> questionsForUsers = new ArrayList<>();
         for(Question q: questionsFromDB) {
            QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());
            questionsForUsers.add(qw);
         }

         return new ResponseEntity<>(questionsForUsers, HttpStatus.OK);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
   }

   public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
      try {
         Optional<Quiz> quiz = quizDao.findById(id);
         List<Question> questions = quiz.get().getQuestions();
         int right = 0;
         int i = 0;
         for(Response response: responses) {
            if(response.getResponse().equals(questions.get(i).getRightAnswer()))
               right++;
            i++;
         }

         return new ResponseEntity<>(right, HttpStatus.OK);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
   }
}
