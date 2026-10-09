// Sample data for the view catalogue (doc 02 section 7). Pure data.
export const notApplicable = ['loading', 'error'];

const categories = [
  { id: 1, name: 'Getting started' },
  { id: 2, name: 'Rules' },
];
const faqs = [
  { id: 1, categoryId: 1, question: 'How do I join the server?', answer: '<p>Copy the server address from the home page and add it in Minecraft.</p>' },
  { id: 2, categoryId: 1, question: 'Do I need an account?', answer: '<p>Register once on the site, then link your in-game name.</p>' },
  { id: 3, categoryId: 2, question: 'What happens if I break a rule?', answer: '<p>Staff will warn you first; repeated breaks lead to a ban.</p>' },
  { id: 4, categoryId: null, question: 'Where can I get help?', answer: '<p>Open a ticket from the support page.</p>' },
];
const config = { showSearch: true, questionLimit: 0 };

/** @type {import('@panomc/plugin-kit').Samples} */
export default {
  filled: { props: { faqs, categories, config } },
  empty: { props: { faqs: [], categories: [], config } },
};
