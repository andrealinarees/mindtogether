import { createRouter, createWebHistory } from "vue-router";
import ErrorNotFoundView from "../views/ErrorNotFoundView.vue";
import LoginForm from "../components/LoginForm.vue";
import ProfileView from "../views/ProfileView.vue";
import ProfileEditView from "../views/ProfileEditView.vue";
import DashboardView from "../views/DashboardView.vue";
import AdminDashboardView from "../views/AdminDashboardView.vue";
import AdminUsersView from "../views/AdminUsersView.vue";
import AdminViewUserHabitsView from "../views/AdminViewUserHabitsView.vue";
import AdminHabitDetailView from "../views/AdminHabitDetailView.vue";
import AdminCommunitiesView from "../views/AdminCommunitiesView.vue";
import CommunityView from "../views/CommunityView.vue";
import CommunityDetailView from "../views/CommunityDetailView.vue";
import CommunityEnterView from "../views/CommunityEnterView.vue";
import CategoriesView from "../views/CategoriesView.vue";

import auth from "@/common/auth";
import { getStore } from "@/common/store";

import habitsRoutes from "@/components/habits/routes.js";
import goalsRoutes from "@/components/goals/routes.js";
import mentalHealthGoalsRoutes from "@/components/mentalHealthGoals/routes.js";
import journalRoutes from "@/components/journal/routes.js";
import wellnessRoutes from "@/components/wellness/routes.js";
import supportCirclesRoutes from "@/components/supportCircles/routes.js";
import rewardsRoutes from "@/components/rewards/routes.js";

const routes = [
  {
    path: "/",
    name: "Login",
    component: LoginForm,
    meta: { public: true, isLoginPage: true }
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: DashboardView
  },
  {
    path: "/admin/dashboard",
    name: "AdminDashboard",
    component: AdminDashboardView,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/users",
    name: "AdminUsers",
    component: AdminUsersView,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/view-user-habits/:userId",
    name: "AdminViewUserHabits",
    component: AdminViewUserHabitsView,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/habit/:id/detail",
    name: "AdminHabitDetail",
    component: AdminHabitDetailView,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/communities",
    name: "AdminCommunities",
    component: AdminCommunitiesView,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/community",
    name: "Community",
    component: CommunityView
  },
  {
    path: "/community/:id",
    name: "CommunityDetail",
    component: CommunityDetailView
  },
  {
    path: "/community/:id/enter",
    name: "CommunityEnter",
    component: CommunityEnterView
  },
  {
    path: "/categories",
    name: "Categories",
    component: CategoriesView
  },
  {
    path: "/profile",
    name: "Profile",
    component: ProfileView
  },
  {
    path: "/profile/edit",
    name: "ProfileEdit",
    component: ProfileEditView
  },
].concat(habitsRoutes, goalsRoutes, mentalHealthGoalsRoutes, journalRoutes, wellnessRoutes, supportCirclesRoutes, rewardsRoutes, [
  {
    path: "/:catchAll(.*)*",
    component: ErrorNotFoundView,
    meta: { public: true }
  }
]);

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach((to, from, next) => {
  // Lo primero que hacemos antes de cargar ninguna ruta es comprobar si
  // el usuario está autenticado (revisando el token)
  auth.isAuthenticationChecked.finally(() => {
    // por defecto, el usuario debe estar autenticado para acceder a las rutas
    const requiresAuth = !to.meta.public;

    const requiredAuthority = to.meta.authority;
    const userIsLogged = getStore().state.user.logged;
    const loggedUserAuthority = getStore().state.user.authority;

    if (requiresAuth) {
      // página privada
      if (userIsLogged) {
        if (requiredAuthority && requiredAuthority != loggedUserAuthority) {
          // usuario logueado pero sin permisos suficientes, le redirigimos a la página de login
          alert("Acceso prohibido para el usuario actual; intenta autenticarte de nuevo");
          auth.logout();
          next("/");
        } else {
          // usuario logueado y con permisos adecuados
          next();
        }
      } else {
        // usuario no está logueado, no puede acceder a la página
        next("/");
      }
    } else {
      // página pública
      if (userIsLogged && to.meta.isLoginPage) {
        // si estamos logueados no hace falta volver a mostrar el login
        // redirigir según el rol del usuario
        if (loggedUserAuthority === 'ADMIN') {
          next({ name: "AdminDashboard", replace: true });
        } else {
          next({ name: "Dashboard", replace: true });
        }
      } else {
        next();
      }
    }
  });
});

export default router;
