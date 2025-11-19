import { useState } from 'react';
import { NoteList } from './components/NoteList';
import { CreateNote } from './components/CreateNote';
import { Mascot } from './components/Mascot';
import { GeneralNoteEditor } from './components/GeneralNoteEditor';
import { ConceptNoteEditor } from './components/ConceptNoteEditor';

type ViewType = 'list' | 'create' | 'mascot' | 'general-note' | 'concept-note';

export default function App() {
  const [currentView, setCurrentView] = useState<ViewType>('list');

  return (
    <div className="min-h-screen bg-[#F8F7FF]">
      {currentView === 'list' && (
        <NoteList 
          onCreateNote={() => setCurrentView('create')}
          onViewMascot={() => setCurrentView('mascot')}
        />
      )}
      {currentView === 'create' && (
        <CreateNote 
          onBack={() => setCurrentView('list')}
          onSelectGeneral={() => setCurrentView('general-note')}
          onSelectConcept={() => setCurrentView('concept-note')}
        />
      )}
      {currentView === 'mascot' && (
        <Mascot 
          onBack={() => setCurrentView('list')}
          onCreateNote={() => setCurrentView('create')}
        />
      )}
      {currentView === 'general-note' && (
        <GeneralNoteEditor onBack={() => setCurrentView('list')} />
      )}
      {currentView === 'concept-note' && (
        <ConceptNoteEditor onBack={() => setCurrentView('list')} />
      )}
    </div>
  );
}