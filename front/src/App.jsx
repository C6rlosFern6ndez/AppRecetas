import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import LoginPage from './pages/Auth/LoginPage'
import SignupPage from './pages/Auth/SignupPage'
import RecipeDetailsPage from './pages/RecipeDetailsPage'
import ProfilePage from './pages/ProfilePage'
import CreateRecipePage from './pages/CreateRecipePage'
import Navbar from './components/layout/Navbar'
import Footer from './components/layout/Footer'
import ProtectedRoute from './components/common/ProtectedRoute'

function App() {
  return (
    <Router>
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/receta/:id" element={<RecipeDetailsPage />} />
          <Route element={<ProtectedRoute />}>
            <Route path="/perfil/:id" element={<ProfilePage />} />
            <Route path="/crear-receta" element={<CreateRecipePage />} />
          </Route>
        </Routes>
      </main>
      <Footer />
    </Router>
  )
}

export default App
